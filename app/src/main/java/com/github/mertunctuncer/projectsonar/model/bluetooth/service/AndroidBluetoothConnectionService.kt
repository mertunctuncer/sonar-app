package com.github.mertunctuncer.projectsonar.model.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.github.mertunctuncer.projectsonar.domain.BluetoothConnection
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData
import com.github.mertunctuncer.projectsonar.domain.InsecureBluetoothConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException


@SuppressLint("MissingPermission")
class AndroidBluetoothConnectionService(
    override val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothScanService: BluetoothScanService
) : BluetoothConnectionService {

    private val _activeConnection: MutableStateFlow<BluetoothConnection?> = MutableStateFlow(null)
    override val connection: StateFlow<BluetoothConnection?> get() = _activeConnection.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()


    private val bluetoothStateReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    BluetoothDevice.EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    if (bluetoothAdapter.bondedDevices?.contains(device) == true) {
                        _isConnected.update { true }
                    }
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    if (bluetoothAdapter.bondedDevices?.contains(device) == true) {
                        _isConnected.update { false }
                    }
                }
            }
        }
    }

    init {
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }


    override suspend fun connect(device: BluetoothDeviceData): BluetoothConnection? {


        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("No BLUETOOTH_CONNECT permission")
        }

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
        val uuid = bluetoothDevice.uuids[0].uuid

        val socket: BluetoothSocket =
            bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
        //connectionSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)

        bluetoothScanService.stopDiscovery()

        return CoroutineScope(Dispatchers.IO).async {
            try {
                socket.connect()
                Log.i(
                    "Bluetooth",
                    "Connected to device ${bluetoothDevice.name} - ${bluetoothDevice.address}"
                )

                val connection = InsecureBluetoothConnection(socket)
                _activeConnection.update {
                    connection
                }
                return@async connection
            } catch (e: IOException) {
                _activeConnection.update { null }
                Log.i(
                    "Bluetooth",
                    "Failed to connect to device ${bluetoothDevice.name} - ${bluetoothDevice.address}"
                )
                return@async null
            }
        }.await()
    }

    override fun closeConnection() {
        _activeConnection.value?.let {
            it.close()
            Log.i("Bluetooth","Closed active bluetooth connection")
        }
    }

    override fun close() {

        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

}