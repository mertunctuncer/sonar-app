package com.github.mertunctuncer.projectsonar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ConnectionCard(
    device: String,
    disconnecting: Boolean,
    onDisconnectClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(40.dp, 0.dp, 0.dp, 0.dp),
                text = "Connected: $device")
            if(disconnecting){
                CircularProgressIndicator(
                    Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp)
                )
            }
            else {
                Button(
                    modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp),
                    onClick = onDisconnectClick
                ) {
                    Text(text = "Disconnect")
                }
            }
        }
    }
}
