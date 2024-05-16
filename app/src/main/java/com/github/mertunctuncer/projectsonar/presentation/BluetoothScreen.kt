package com.github.mertunctuncer.projectsonar.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice
import com.github.mertunctuncer.projectsonar.domain.BluetoothDeviceData
import com.github.mertunctuncer.projectsonar.ui.component.BluetoothDeviceList
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
    Scaffold(
        topBar = {
            ThemedTopBar(
                modifier = modifier,
                text = "Connect Device",
                navIcon = Icons.Default.ArrowBack,
                navContent = "Return to radar",
                onNavClick = onNavClick
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




/*
@Composable
fun ScanS0tartStopButton(bluetoothViewModel: BluetoothViewModel) {
    var enabled by remember { mutableStateOf(false) }
    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (!enabled)
            Button(
                modifier = Modifier
                    .size(230.dp, 80.dp)
                    .padding(15.dp),
                onClick = {
                    enabled = !enabled
                    bluetoothViewModel.startScan()
                }
            ) {
                Text(
                    text = "Start scan",
                    fontSize = 16.sp
                )
            }
        else
            ElevatedButton(
                modifier = Modifier
                    .size(230.dp, 80.dp)
                    .padding(15.dp),
                onClick = {
                    enabled = !enabled
                    bluetoothViewModel.stopScan()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stop scan",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp, 32.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
    }
}
 */