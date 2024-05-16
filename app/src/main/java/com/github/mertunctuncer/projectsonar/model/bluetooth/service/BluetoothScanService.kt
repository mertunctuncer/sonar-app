package com.github.mertunctuncer.projectsonar.model.bluetooth.service

import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.utilities.ContextOwner
import kotlinx.coroutines.flow.StateFlow

interface BluetoothScanService : AutoCloseable, ContextOwner{

    val isScanning: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()
}