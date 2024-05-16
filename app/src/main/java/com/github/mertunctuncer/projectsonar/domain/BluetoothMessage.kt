package com.github.mertunctuncer.projectsonar.domain


data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)