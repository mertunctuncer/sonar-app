package com.github.mertunctuncer.projectsonar.domain

import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


class InsecureBluetoothConnection(
    private val bluetoothSocket: BluetoothSocket
) : BluetoothConnection {

    private val active = AtomicBoolean(true);

    override val receivedMessages: Flow<String> = flow {
        if (!bluetoothSocket.isConnected) return@flow

        val buffer = ByteArray(1024)
        var index = 0;
        while (active.get()) {
            try {
                buffer[index] = bluetoothSocket.inputStream.read().toByte()
                if(buffer[index] == '\n'.code.toByte()) {
                    val message = String(buffer, 0, index)
                    Log.i("Bluetooth", "Received message: \"$message\"")
                    emit(message)
                    index = 0
                } else index++
            } catch (e: IOException) {
                Log.i("Bluetooth", "Failed to read message!")
            }
        }
    }.onCompletion {
        bluetoothSocket.close()
    }.flowOn(Dispatchers.IO)

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