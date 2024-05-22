package com.github.mertunctuncer.projectsonar.domain



sealed interface BluetoothMessage {
    val bytes: ByteArray
}

sealed interface ReceivedBluetoothMessage



data class ControlMessage(
    val protocol: String
): BluetoothMessage {
    override val bytes = protocol.toByteArray()
}

class TextMessage(
    message: String
) : ReceivedBluetoothMessage {
    val parsedMessage = message.substring(2, message.lastIndex)
}

class DataMessage(
    message: String
) : ReceivedBluetoothMessage {
    val angle: Float
    val distance: Float
    init {
        val data = message.substring(2, message.lastIndex)
        angle = data.substringBefore(' ').toFloat()
        distance = data.substringAfter(' ').toFloat()
    }
}