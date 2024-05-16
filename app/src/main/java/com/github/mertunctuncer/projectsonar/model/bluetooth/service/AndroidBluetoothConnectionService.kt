package com.github.mertunctuncer.projectsonar.model.bluetooth.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
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
import com.github.mertunctuncer.projectsonar.utilities.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException


@SuppressLint("MissingPermission")
class AndroidBluetoothConnectionService(
    override val context: Context,
    private val lifecycleScope: CoroutineScope,
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
                    EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_DEVICE)
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

    private val pairReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if(ACTION_BOND_STATE_CHANGED != action) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_DEVICE)
            }?.let {
                if(it.bondState == BluetoothDevice.BOND_BONDING) {
                    Log.i("Bluetooth", "Pair initiated with ${it.name}")
                    return
                }
                if(it.bondState == BluetoothDevice.BOND_BONDED) {
                    Log.i("Bluetooth", "Paired with device: ${it.name}")
                    CoroutineScope(Dispatchers.Main).launch {
                        connect(it.toDomain())
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

        context.registerReceiver(
            pairReceiver,
            IntentFilter(ACTION_BOND_STATE_CHANGED)
        )
    }


    override suspend fun connect(device: BluetoothDeviceData): BluetoothConnection? {
        Log.i("Bluetooth", "Attempting connection with ${device.name}")


        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("No BLUETOOTH_CONNECT permission")
        }

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)


        val uuid = bluetoothDevice.uuids[0].uuid

        val socket: BluetoothSocket =
            bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
        //connectionSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)

        bluetoothScanService.stopDiscovery()


        return lifecycleScope.async(Dispatchers.IO) {
            try {
                socket.connect()
                Log.i(
                    "Bluetooth",
                    "Connected to device ${bluetoothDevice.name} - ${bluetoothDevice.address}"
                )

                val connection = InsecureBluetoothConnection(socket, lifecycleScope)
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

    override fun pairAndConnect(device: BluetoothDeviceData) {
        Log.i("Bluetooth", "Pairing with ${device.name}")
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException("No BLUETOOTH_CONNECT permission")
        }

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
        bluetoothDevice.createBond()
    }

    override fun closeConnection() {
        _activeConnection.value?.let {
            it.close()
            Log.i("Bluetooth","Closed active bluetooth connection")
        }
    }

    override fun close() {
        context.unregisterReceiver(bluetoothStateReceiver)
        context.unregisterReceiver(pairReceiver)
        closeConnection()
    }

}