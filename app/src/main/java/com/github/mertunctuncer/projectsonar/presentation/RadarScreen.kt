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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mertunctuncer.projectsonar.domain.BluetoothProtocol
import com.github.mertunctuncer.projectsonar.domain.ControlMessage
import com.github.mertunctuncer.projectsonar.domain.SonarData
import com.github.mertunctuncer.projectsonar.model.bluetooth.BluetoothController
import com.github.mertunctuncer.projectsonar.model.bluetooth.service.BluetoothConnectionService
import com.github.mertunctuncer.projectsonar.ui.component.ConnectionCard
import com.github.mertunctuncer.projectsonar.ui.theme.Purple40
import com.github.mertunctuncer.projectsonar.ui.theme.Purple80
import com.github.mertunctuncer.projectsonar.ui.theme.PurpleGrey40
import kotlinx.coroutines.runBlocking
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SonarScreen(viewModel: RadarViewModel, onNavClick: () -> Unit) {
    val state by viewModel.state.collectAsState()
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
                    IconButton(onClick = onNavClick) {
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
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val points = mutableListOf<Pair<Float, Float>>()
            points.add(Pair(10f, 15f))
            points.add(Pair(94f, 14f))
            DrawRadar(180f, 30f, state.lastPoints)
            Text(
                text = "Distance: ${state.lastDistance} cm",
                fontSize = 30.sp
            )
        }

    }
}

@Composable
fun DrawRadar(angle: Float, maxDistance: Float, detectedPoints: List<SonarData>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(30.dp, 30.dp),
        onDraw = {

            drawArc(Color.Black, 0f, -180f, true)
            drawArc(Color.DarkGray, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.01f, this.size.height * 0.01f),
                size = Size(this.size.width * 0.98f, this.size.height * 0.98f)
            )
            drawArc(Color.Black, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.20f, this.size.height * 0.20f),
                size = Size(this.size.width * 0.60f, this.size.height * 0.60f)
            )
            drawArc(Color.DarkGray, 0f, -180f, true,
                topLeft = Offset(this.size.width * 0.21f, this.size.height * 0.21f),
                size = Size(this.size.width * 0.58f, this.size.height * 0.58f)
            )

            drawArc(Color.Black, 0f, -1f, true,)
            drawArc(Color.Black, -30f, -1f, true,)
            drawArc(Color.Black, -60f, -1f, true,)
            drawArc(Color.Black, -90f, -1f, true,)
            drawArc(Color.Black, -120f, -1f, true,)
            drawArc(Color.Black, -150f, -1f, true,)
            drawArc(Color.Black, -180f, -1f, true,)



            detectedPoints.forEachIndexed { index, sonarData ->
                val ratio = (index.toFloat() + 1) / detectedPoints.size
                if(sonarData.distance > maxDistance) {
                    drawArc(
                        Color.Green, -1 * (sonarData.angle -1), 2f, true,
                        size = this.size,
                        alpha = ratio,
                        blendMode = BlendMode.Overlay
                    )
                } else {
                    val length = sonarData.distance / maxDistance
                    drawArc(
                        Color.Red, -1 * (sonarData.angle -1), 2f, true,
                        size = this.size,
                        alpha = ratio,
                        blendMode = BlendMode.Color
                    )
                    drawArc(
                        Color.Green, -1 * (sonarData.angle -1), 2f, true,
                        size = Size(this.size.width * length, this.size.height * length),
                        topLeft = Offset(this.size.width * (1 - length) / 2, this.size.height * (1-length) / 2),
                        alpha = ratio,
                        blendMode = BlendMode.Color
                    )
                }
            }

        }
    )
}

@Composable
fun ControlButtons(controller: BluetoothController) {
    var started by remember { mutableStateOf(false) }
    val connection by controller.activeConnection.collectAsState()

    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if(started) StopButton {
            runBlocking {
                started = !started
                connection?.sendMessage(ControlMessage(BluetoothProtocol.AUTO_TURN_OFF))
            }
        }
        else StartButton {
            runBlocking {
                started = !started
                connection?.sendMessage(ControlMessage(BluetoothProtocol.AUTO_TURN_ON))
            }
        }
        ResetButton {
            runBlocking {
                connection?.sendMessage(ControlMessage(BluetoothProtocol.RESET_ANGLE))
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
