package com.github.mertunctuncer.projectsonar.presentation

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData
import com.github.mertunctuncer.projectsonar.model.bluetooth.BluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BluetoothViewModel(
    private val bluetoothController: BluetoothController
) : ViewModel() {
    data class BluetoothUIState(
        val scannedDevices: List<BluetoothDeviceData> = emptyList(),
        val pairedDevices: List<BluetoothDeviceData> = emptyList(),
        val isConnected: Boolean = false,
        val isScanning: Boolean = false,
        val isConnecting: Boolean = false,
        val errorMessage: String? = null,
    )

    private val _state = MutableStateFlow(BluetoothUIState())

    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        bluetoothController.isConnected,
        bluetoothController.isScanning,
        _state
    ) { scannedDevices, pairedDevices, isConnected, isScanning, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            isConnected = isConnected,
            isScanning = isScanning
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun connect(device: BluetoothDeviceData) = viewModelScope.launch {
        _state.update { it.copy(isConnecting = true) }

        bluetoothController.pairedDevices.collectLatest {pairedDevices ->
            if(!pairedDevices.contains(device)) {
                bluetoothController.pairAndConnect(device)
                return@collectLatest
            }

            val result = bluetoothController.connect(device)
            if (result == null) _state.update {
                it.copy(
                    errorMessage = "Failed to connect to ${device.name}!"
                )
            } else {
                _state.update {
                    it.copy(
                        isConnected = true,
                        isConnecting = false,
                        errorMessage = null
                    )
                }
            }
        }


    }


    fun disconnect() {
        bluetoothController.disconnect()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }



    fun startScan() {
        _state.update {
            it.copy(isScanning = true)
        }
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        _state.update {
            it.copy(isScanning = false)
        }
        bluetoothController.stopDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.close()
    }

}