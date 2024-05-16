package com.github.mertunctuncer.projectsonar.model.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData
import com.github.mertunctuncer.projectsonar.utilities.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


@SuppressLint("MissingPermission")
class AndroidBluetoothScanService(
    override val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
): BluetoothScanService {



    private val foundDeviceReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND != action) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    BluetoothDevice.EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }?.toDomain()?.let {
                Log.i("Bluetooth", "Found device: ${it.name}")
                if (!_scannedDevices.value.contains(it))
                    _scannedDevices.update { devices ->
                        return@update devices + it
                    }
            }
        }
    }

    init {
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
    }
    private val _isScanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> get() = _isScanning.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceData>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceData>> get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceData>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceData>> get() = _pairedDevices.asStateFlow()


    private fun fetchPairedDevices() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return

        bluetoothAdapter.bondedDevices.map { it.toDomain() }.also { devices ->
            _pairedDevices.update { devices }
        }
    }

    override fun startDiscovery() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return

        fetchPairedDevices()
        _scannedDevices.update { emptyList() }

        Log.i("Bluetooth", "Started discovery.")
        bluetoothAdapter.startDiscovery()
        _isScanning.update { true }
    }

    override fun stopDiscovery() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return

        Log.i("Bluetooth", "Stopped discovery.")
        bluetoothAdapter.cancelDiscovery()
        _isScanning.update { false }
    }

    override fun close() {
        context.unregisterReceiver(foundDeviceReceiver)
        stopDiscovery()
    }
}