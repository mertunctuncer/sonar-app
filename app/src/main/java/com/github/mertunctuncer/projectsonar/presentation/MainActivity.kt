@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mertunctuncer.projectsonar.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.AndroidBluetoothConnectionService
import com.github.mertunctuncer.projectsonar.ui.theme.ProjectSonarTheme


class MainActivity : ComponentActivity() {




    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }
    private val isBluetoothEnabled get() = bluetoothAdapter?.isEnabled == true

    private val bluetoothController by lazy { AndroidBluetoothConnectionService(this, bluetoothAdapter) }
    private val bluetoothViewModel by lazy { BluetoothViewModel(bluetoothController) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestStartBluetooth()
        requestBluetoothPermission()

        bluetoothController.startBluetoothServer()
        setContent {
            var sonarScreenActive by remember {
                mutableStateOf(false)
            }
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {

            }
            ProjectSonarTheme {
                if(sonarScreenActive) SonarScreen(bluetoothController) {
                    sonarScreenActive = !sonarScreenActive
                }
                else BluetoothScreen(bluetoothViewModel, {
                    sonarScreenActive = !sonarScreenActive
                }, {
                    bluetoothViewModel.connect(it)
                })
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        bluetoothController.close()
    }

    private fun requestStartBluetooth() {
        if (isBluetoothEnabled) return

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.i("Bluetooth", "Permission successful.")
            } else {
                Log.i("Bluetooth", "Permission denied!")
            }
        }

        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activityResultLauncher.launch(enableBluetoothIntent)
    }


    private fun requestBluetoothPermission() {

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            it.forEach { (permission, result) ->

                if(!result) Log.i("Bluetooth", "Failed to request $permission")
                else Log.i("Bluetooth", "Successful request: $permission")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }
    }


}


