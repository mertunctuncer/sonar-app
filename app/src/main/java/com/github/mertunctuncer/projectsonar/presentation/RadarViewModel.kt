package com.github.mertunctuncer.projectsonar.presentation

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mertunctuncer.projectsonar.R
import com.github.mertunctuncer.projectsonar.domain.SonarData
import com.github.mertunctuncer.projectsonar.model.bluetooth.BluetoothController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


class RadarViewModel(
    private val bluetoothController: BluetoothController,
    private val context: Context
) : ViewModel() {

    data class RadarUIState(
        val lastPoints: List<SonarData> = emptyList(),
        val lastDistance: Float? = null,
        val isConnected: Boolean = false
    )

    var vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    val mMediaPlayer = MediaPlayer.create(context, R.raw.beep)

    private var _state = MutableStateFlow(RadarUIState())

    val state = combine(
        bluetoothController.lastPoints,
        bluetoothController.isConnected,
        _state
    ) { lastPoints, isConnected, state ->

        val newState = state.copy (
            lastPoints = lastPoints,
            lastDistance = if(lastPoints.isEmpty()) null else (lastPoints[lastPoints.lastIndex].distance),
            isConnected = isConnected
        )
        newState.lastDistance?.let {
            if(it < 15) {
                Log.i("Vibrator", "Vibrating!")
                val effect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator?.vibrate(effect)
                mMediaPlayer.start()
            };
        }

        return@combine newState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)
}