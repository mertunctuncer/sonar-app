package com.github.mertunctuncer.projectsonar.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun ScanStartStopButton(
    modifier: Modifier = Modifier,
    scanning: Boolean,
    onScanStartClick: () -> Unit = {},
    onScanStopClick: () -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!scanning)
            Button(
                modifier = modifier,
                onClick = onScanStartClick
            ) {
                Text(
                    text = "Scan",
                    fontSize = 16.sp
                )
            }
        else
            ElevatedButton(
                modifier = modifier,
                onClick = onScanStopClick
            ) {
                Text(
                    text = "Stop",
                    fontSize = 16.sp
                )
            }
    }
}