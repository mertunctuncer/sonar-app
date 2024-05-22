package com.github.mertunctuncer.projectsonar.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mertunctuncer.projectsonar.ui.component.BluetoothDeviceList
import com.github.mertunctuncer.projectsonar.ui.component.ConnectionCard
import com.github.mertunctuncer.projectsonar.ui.component.ProcessingDialog
import com.github.mertunctuncer.projectsonar.ui.component.ErrorDialog
import com.github.mertunctuncer.projectsonar.ui.component.ScanStartStopButton
import com.github.mertunctuncer.projectsonar.ui.component.ThemedTopBar
import com.github.mertunctuncer.projectsonar.viewmodel.BluetoothViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScreen(
    modifier: Modifier = Modifier,
    viewModel: BluetoothViewModel,
    onNavClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            ThemedTopBar(
                modifier = modifier,
                title = {
                    Column {
                        Text("Bluetooth")
                        Text(
                            text = "Click a device to connect",
                            textAlign = TextAlign.Right,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraLight,
                            fontStyle = FontStyle.Italic
                        )
                    }

                },
                navIcon = Icons.Default.ArrowBack,
                navContent = "Return to radar",
                onNavClick = onNavClick,
                actions = {
                    if(state.isScanning) CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp, 40.dp)
                            .padding(10.dp, 10.dp)
                    )
                    ScanStartStopButton(
                        modifier = Modifier.size(100.dp, 40.dp),
                        scanning = state.isScanning,
                        onScanStartClick = {
                            viewModel.startScan()
                        },
                        onScanStopClick = {
                            viewModel.stopScan()
                        }
                    )
                }
            )
        },
    ) { innerPadding ->

        state.errorMessage?.let{
            ErrorDialog(it) { viewModel.removeErrorDialog() }
        }
        if(state.isConnecting) ProcessingDialog("Connecting...")

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if(state.isConnected) {
                state.connectedDevice?.let {
                    ConnectionCard(it, state.isDisconnecting) {
                        viewModel.disconnect()
                    }
                }
            }

            BluetoothDeviceList (
                modifier = modifier,
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onDeviceClick = { viewModel.connect(it) }
            )

        }
    }
}