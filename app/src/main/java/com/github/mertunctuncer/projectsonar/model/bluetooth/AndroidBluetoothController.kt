package com.github.mertunctuncer.projectsonar.model.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.AndroidBluetoothConnectionService
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.AndroidBluetoothScanService
import com.github.mertunctuncer.projectsonar.domain.BluetoothConnection
import com.github.mertunctuncer.projectsonar.domain.SonarData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidBluetoothController(
    override val context: Context,
    bluetoothAdapter: BluetoothAdapter,
    lifecycleScope: CoroutineScope
) : BluetoothController {

    private val bluetoothScanService = AndroidBluetoothScanService(context, bluetoothAdapter)
    private val bluetoothConnectionService = AndroidBluetoothConnectionService(context, lifecycleScope, bluetoothAdapter, bluetoothScanService, this)

    override val activeConnection: StateFlow<BluetoothConnection?> get() =  bluetoothConnectionService.connection

    override val scannedDevices: StateFlow<List<BluetoothDevice>> get() = bluetoothScanService.scannedDevices
    override val pairedDevices: StateFlow<List<BluetoothDevice>> get() = bluetoothScanService.pairedDevices
    override val lastPoints: MutableStateFlow<List<SonarData>> = MutableStateFlow(emptyList())

    override fun startDiscovery() = bluetoothScanService.startDiscovery()
    override fun stopDiscovery() = bluetoothScanService.stopDiscovery()

    override fun close() {
        activeConnection.value?.close()
        bluetoothConnectionService.close()
        bluetoothScanService.close()
    }

    override val isConnected: StateFlow<Boolean> get() = bluetoothConnectionService.isConnected
    override val isScanning: StateFlow<Boolean> get() = bluetoothScanService.isScanning

    override suspend fun connect(device: BluetoothDevice): BluetoothConnection? = bluetoothConnectionService.connect(device)
    override suspend fun pairAndConnect(device: BluetoothDevice) = bluetoothConnectionService.pairAndConnect(device)

    override fun disconnect() = bluetoothConnectionService.closeConnection()
}