package com.github.mertunctuncer.projectsonar.model.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.AndroidBluetoothConnectionService
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.AndroidBluetoothScanService
import com.github.mertunctuncer.projectsonar.domain.BluetoothConnection
import kotlinx.coroutines.flow.StateFlow

class AndroidBluetoothController(
    override val context: Context,
    bluetoothAdapter: BluetoothAdapter
) : BluetoothController {

    private val bluetoothScanService = AndroidBluetoothScanService(context, bluetoothAdapter)
    private val bluetoothConnectionService = AndroidBluetoothConnectionService(context, bluetoothAdapter, bluetoothScanService)

    override val activeConnection: StateFlow<BluetoothConnection?> = bluetoothConnectionService.connection

    override val scannedDevices: StateFlow<List<BluetoothDevice>> = bluetoothScanService.scannedDevices
    override val pairedDevices: StateFlow<List<BluetoothDevice>> = bluetoothScanService.pairedDevices

    override fun startDiscovery() = bluetoothScanService.startDiscovery()
    override fun stopDiscovery() = bluetoothScanService.stopDiscovery()

    override fun close() {
        activeConnection.value?.close()
        bluetoothConnectionService.close()
        bluetoothScanService.close()
    }

    override val isConnected: StateFlow<Boolean> = bluetoothConnectionService.isConnected

    override suspend fun connect(device: BluetoothDevice): BluetoothConnection? = bluetoothConnectionService.connect(device)
    override fun disconnect() = bluetoothConnectionService.closeConnection()


}