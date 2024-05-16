package com.github.mertunctuncer.projectsonar.presentation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.BluetoothConnectionService
import com.github.mertunctuncer.projectsonar.ui.theme.Purple40
import com.github.mertunctuncer.projectsonar.ui.theme.Purple80
import com.github.mertunctuncer.projectsonar.ui.theme.PurpleGrey40
import kotlinx.coroutines.runBlocking
import kotlin.math.cos
import kotlin.math.sin

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SonarScreen(controller: BluetoothConnectionService, onClick: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Sonar Control")
                },
                navigationIcon = {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Bluetooth Settings"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            val points = mutableListOf<Pair<Float, Float>>()
            points.add(Pair(10f, 15f))
            points.add(Pair(94f, 14f))
            DrawRadar(180f, 30f, points)
            ControlButtons(controller)
        }

    }
}

@Composable
fun DrawRadar(angle: Float, maxDistance: Float, detectedPoints: List<Pair<Float, Float>>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(30.dp, 30.dp),
        onDraw = {

            drawArc(Purple40, 0f, -180f, true)
            drawArc(PurpleGrey40, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.01f, this.size.height * 0.01f),
                size = Size(this.size.width * 0.98f, this.size.height * 0.98f)
            )
            drawArc(Purple40, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.20f, this.size.height * 0.20f),
                size = Size(this.size.width * 0.60f, this.size.height * 0.60f)
            )
            drawArc(PurpleGrey40, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.21f, this.size.height * 0.21f),
                size = Size(this.size.width * 0.58f, this.size.height * 0.58f)
            )
            drawArc(Purple80, (-1) * (angle + 4f),  8f, true,
                blendMode = BlendMode.Overlay
            )
            val radius = size.width / 2
            Log.i("Sonar radius", "$radius")

            val points = detectedPoints.filter { it.second < maxDistance }.map {
                val ratio = it.second / maxDistance
                val size = ratio * radius

                val offset = Offset(radius + cos(it.first) * size, radius - sin(it.first) * size)
                Log.i("Sonar", offset.toString())
                return@map offset
            }
            drawPoints(points, PointMode.Points, Purple80)
        }
    )
}

@Composable
fun ControlButtons(controller: BluetoothConnectionService) {
    var started by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if(started) StopButton {
            runBlocking {
                started = !started
                controller.trySendMessage("1")
            }
        }
        else StartButton {
            runBlocking {
                started = !started
                controller.trySendMessage("0")
            }
        }
        ResetButton {
            runBlocking {
                controller.trySendMessage("2")
            }
        }
    }
}

@Composable
fun StartButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier
            .size(150.dp, 40.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start Sonar"
            )
            Text("Start")
        }
    }
}


@Composable
fun StopButton( modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier
            .size(150.dp, 40.dp),
        onClick = onClick ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Stop Sonar"
            )
            Text("Stop")
        }
    }
}

@Composable
fun ResetButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier
            .size(150.dp, 40.dp),
        onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset Sonar"
            )
            Text("Reset")
        }
    }
}
*/