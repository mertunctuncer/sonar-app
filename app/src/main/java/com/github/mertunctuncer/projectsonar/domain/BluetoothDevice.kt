package com.github.mertunctuncer.projectsonar.domain

typealias BluetoothDeviceData = BluetoothDevice

data class BluetoothDevice (
    val name: String?,
    val address: String
)
