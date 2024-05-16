package com.github.mertunctuncer.projectsonar.ui.component


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mertunctuncer.projectsonar.domain.BluetoothDevice



@Composable
fun BluetoothDeviceList (
    modifier: Modifier = Modifier,
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card (
            modifier = modifier.padding(10.dp)
        ) {
            DeviceList(
                modifier = Modifier.padding(15.dp),
                title = "Paired Devices",
                devices = pairedDevices,
                onDeviceClick = onDeviceClick
            )
        }
        Card (
            modifier = modifier.padding(10.dp)
        ) {
            DeviceList(
                modifier = Modifier.padding(15.dp),
                title = "Scanned Devices",
                devices = scannedDevices
            )
        }
    }
}


@Composable
private fun DeviceList(
    modifier: Modifier = Modifier,
    title: String,
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { Header(title) }
        item { Divider(
            Modifier.padding(20.dp, 0.dp, 20.dp, 10.dp),
            color = MaterialTheme.colorScheme.onSurface
            )
        }
        items(devices.size) { index ->
            ListNode(
                device = devices[index],
                onClick = onDeviceClick)
        }
    }
}


@Composable
private fun ListNode(
    modifier: Modifier = Modifier,
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(20.dp, 2.dp, 0.dp, 2.dp)
            .height(30.dp)
            .clickable { onClick(device) }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Icon(
            modifier = Modifier.size(13.dp, 13.dp),
            imageVector = Icons.Sharp.KeyboardArrowRight,
            contentDescription = "Paired Devices",
        )
        Text(
            text = device.name ?: "Undefined [${device.address}]",
            modifier = Modifier
                .fillMaxWidth(),
            fontWeight = FontWeight.ExtraLight
        )
    }
}



@Composable
private fun Header(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(23.dp, 5.dp, 0.dp, 10.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}