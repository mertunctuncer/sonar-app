package com.github.mertunctuncer.projectsonar.model.bluetooth

import com.github.mertunctuncer.projectsonar.domain.BluetoothConnection
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.utilities.ContextOwner
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController : ContextOwner, AutoCloseable {
    val activeConnection: StateFlow<BluetoothConnection?>

    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    val isConnected: StateFlow<Boolean>
    val isScanning: StateFlow<Boolean>

    suspend fun connect(device: BluetoothDevice): BluetoothConnection?
    suspend fun pairAndConnect(device: BluetoothDevice)
    fun disconnect()
}