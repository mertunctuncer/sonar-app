package com.github.mertunctuncer.projectsonar.utilities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDomain(): BluetoothDeviceData {
    return BluetoothDeviceData(
        name = name,
        address = address
    )
}