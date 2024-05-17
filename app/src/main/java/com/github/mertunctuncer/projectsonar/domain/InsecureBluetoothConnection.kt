package com.github.mertunctuncer.projectsonar.domain

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.github.mertunctuncer.projectsonar.model.bluetooth.BluetoothController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


class InsecureBluetoothConnection(

    private val bluetoothSocket: BluetoothSocket,
    private val lifecycleScope: CoroutineScope,
    private val bluetoohController: BluetoothController
) : BluetoothConnection {
    private val active = AtomicBoolean(true)
    init {
        lifecycleScope.launch(Dispatchers.IO) {
            if (!bluetoothSocket.isConnected) {
                Log.i("Bluetooth", "Bluetooth socket is no longer connected, stopping the thread.")
                active.set(false)
                return@launch
            }

            val buffer = ByteArray(1024)
            var index = 0
            while (active.get()) {
                try {
                    buffer[index] = bluetoothSocket.inputStream.read().toByte()
                    if(buffer[index] == '\n'.code.toByte()) {
                        val message = String(buffer, 0, index)
                        if(message.first() == 'd') {
                            val message = DataMessage(message)
                            val sonarData = SonarData(message.angle, message.distance)
                            bluetoohController.lastPoints.update {
                                if(it.size > 10) {
                                    return@update it.subList(1, it.size) + sonarData
                                } else
                                    return@update it + sonarData
                            }
                        }
                        else if(message.first() == 'm') {
                            val message = TextMessage(message)
                            Log.i("Bluetooth", "Received message: \"${message.parsedMessage}\"")
                        }

                        index = 0
                    } else index++
                } catch (e: IOException) {
                    Log.i("Bluetooth", "Failed to read message!")
                    active.set(false)
                    return@launch
                } catch (e: Exception) {
                    // ignore
                }
            }
        }.invokeOnCompletion {
            bluetoothSocket.close()
        }
    }

    override fun sendMessage(message: BluetoothMessage) = lifecycleScope.launch {
        if (!active.get()) throw IllegalStateException("Connection has been closed!")

        try {
            bluetoothSocket.outputStream.write(message.bytes);
            Log.i("Bluetooth", "Sent message: $message")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Failed to send message!", e)
        }
    }


    override fun close() {
        active.set(false)
    }
}