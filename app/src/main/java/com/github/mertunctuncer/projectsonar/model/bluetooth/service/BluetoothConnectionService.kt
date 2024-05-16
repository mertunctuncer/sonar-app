package com.github.mertunctuncer.projectsonar.model.bluetooth.service

import com.github.mertunctuncer.projectsonar.domain.BluetoothConnection
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.utilities.ContextOwner
import kotlinx.coroutines.flow.StateFlow

interface BluetoothConnectionService : AutoCloseable, ContextOwner {

    val isConnected: StateFlow<Boolean>
    val connection: StateFlow<BluetoothConnection?>

    suspend fun connect(device: BluetoothDevice) : BluetoothConnection?
    fun pairAndConnect(device: BluetoothDevice)
    fun closeConnection()
}