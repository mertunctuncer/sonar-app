package com.github.mertunctuncer.projectsonar.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.ui.component.BluetoothDeviceList
import com.github.mertunctuncer.projectsonar.ui.component.ScanStartStopButton
import com.github.mertunctuncer.projectsonar.ui.component.ThemedTopBar
import com.github.mertunctuncer.projectsonar.ui.theme.ProjectSonarTheme


@Preview
@Composable
fun PreviewBluetoothScreen() {

    ProjectSonarTheme {
        val state = BluetoothViewModel.BluetoothUIState(
            pairedDevices = listOf(
                BluetoothDevice("Name1", "Address1"),
                BluetoothDevice("Name2", "Address1"),
                BluetoothDevice("Name3", "Address1"),
                BluetoothDevice("Name4", "Address1"),
                BluetoothDevice("Name5", "Address1"),
                BluetoothDevice("Name6", "Address1"),
                BluetoothDevice("Name7", "Address1"),
            ),
            scannedDevices = listOf(
                BluetoothDevice("Name1", "Address1"),
                BluetoothDevice("Name2", "Address1"),
                BluetoothDevice("Name3", "Address1"),
                BluetoothDevice("Name4", "Address1"),
                BluetoothDevice("Name5", "Address1"),
                BluetoothDevice("Name6", "Address1"),
                BluetoothDevice("Name7", "Address1"),
            )
        )
        BluetoothScreen(
            modifier = Modifier,
            state = state,
            onNavClick = {},
            onDeviceClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScreen(
    modifier: Modifier = Modifier,
    state: BluetoothViewModel.BluetoothUIState,
    onNavClick: () -> Unit = {},
    onDeviceClick: (BluetoothDevice) -> Unit = {}
) {
    var scanning by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ThemedTopBar(
                modifier = modifier,
                title = {
                    Column {
                        Text("Bluetooth Connection")
                        Text(
                            text = "Click a device to connect.",
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

                    if(scanning) CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp, 40.dp)
                            .padding(10.dp, 10.dp)
                    )
                    ScanStartStopButton(
                        modifier = Modifier.size(100.dp, 40.dp),
                        scanning = scanning,
                        onScanStartClick = {
                            scanning = true
                        },
                        onScanStopClick = {
                            scanning = false
                        }
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            BluetoothDeviceList (
                modifier = modifier,
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onDeviceClick = onDeviceClick
            )

        }
    }
}