package com.github.mertunctuncer.projectsonar.domain

import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


class InsecureBluetoothConnection(

    private val bluetoothSocket: BluetoothSocket,
    lifecycleScope: CoroutineScope
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
                        Log.i("Bluetooth", "Received message: \"$message\"")

                        index = 0
                    } else index++
                } catch (e: IOException) {
                    Log.i("Bluetooth", "Failed to read message!")
                    active.set(false)
                    return@launch

                }
            }
        }.invokeOnCompletion {
            bluetoothSocket.close()
        }
    }

    override fun sendMessage(message: String) = CoroutineScope(Dispatchers.IO).launch {
        if (!active.get()) throw IllegalStateException("Connection has been closed!")

        val bytes = message.toByteArray()
        try {
            bluetoothSocket.outputStream.write(bytes)
        } catch (e: IOException) {
            Log.e("Bluetooth", "Failed to send message!", e)
        }
    }


    override fun close() {
        active.set(false)
    }
}