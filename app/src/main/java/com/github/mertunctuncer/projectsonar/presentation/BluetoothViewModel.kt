package com.github.mertunctuncer.projectsonar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData
import com.github.mertunctuncer.projectsonar.model.bluetooth.BluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
        val isConnecting: Boolean = false,
        val errorMessage: String? = null,
    )
    /*



    private val _state = MutableStateFlow(BluetoothUIState())

    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun connect(device: BluetoothDeviceData) = viewModelScope.launch {
        _state.update { it.copy(isConnecting = true) }
        val result = bluetoothController.connect(device).await()
        if (result.isFailure) _state.update {
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


    fun disconnect() {
        bluetoothController.closeConnection()
        _state.update { it.copy(
            isConnecting = false,
            isConnected = false
        ) }
    }



    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.close()
    }


     */
}