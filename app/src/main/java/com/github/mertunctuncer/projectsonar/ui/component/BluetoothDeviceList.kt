package com.github.mertunctuncer.projectsonar.ui.component

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
    LazyColumn(
        modifier = modifier
    ) {

        item {
            DeviceList (
                modifier = modifier,
                title = "Paired Devices",
                devices = pairedDevices,
                onDeviceClick = onDeviceClick
            )
        }
        item {
            Spacer (
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
        item {
            DeviceList(
                modifier = modifier,
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
        item { Divider(Modifier.padding(20.dp)) }
        items(devices.size) { index ->
            ListNode(modifier, devices[index], onDeviceClick)
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
            .clickable { onClick(device) }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Icon(
            imageVector = Icons.Sharp.KeyboardArrowRight,
            contentDescription = "Paired Devices",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = device.name ?: "Undefined [${device.address}]",
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface,
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily(Typeface.MONOSPACE)
        )
    }
}



@Composable
private fun Header(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(32.dp, 16.dp)
    )
}