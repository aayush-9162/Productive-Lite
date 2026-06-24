package com.example.notemoon.tools.presentation.timer

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Countdown timer with start/pause/reset; vibrates when it reaches zero. */
@HiltViewModel
class TimerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var tickJob: Job? = null
    private var targetEndTime = 0L

    fun onHoursChange(value: String) = updateInput { it.copy(inputHours = sanitize(value, 99)) }
    fun onMinutesChange(value: String) = updateInput { it.copy(inputMinutes = sanitize(value, 59)) }
    fun onSecondsChange(value: String) = updateInput { it.copy(inputSeconds = sanitize(value, 59)) }

    private fun updateInput(transform: (TimerState) -> TimerState) {
        if (!_state.value.isActive) _state.update(transform)
    }

    fun startOrPause() {
        val s = _state.value
        when {
            s.isFinished -> Unit
            !s.isActive -> startNew()
            s.isRunning -> pause()
            else -> resume()
        }
    }

    private fun startNew() {
        val s = _state.value
        val total = ((s.inputHours.toLongOrNull() ?: 0) * 3600 +
            (s.inputMinutes.toLongOrNull() ?: 0) * 60 +
            (s.inputSeconds.toLongOrNull() ?: 0)) * 1000
        if (total <= 0) return
        _state.update {
            it.copy(totalMillis = total, remainingMillis = total, isActive = true, isFinished = false)
        }
        resume()
    }

    private fun resume() {
        targetEndTime = System.currentTimeMillis() + _state.value.remainingMillis
        _state.update { it.copy(isRunning = true) }
        tickJob = viewModelScope.launch {
            while (isActive) {
                val remaining = targetEndTime - System.currentTimeMillis()
                if (remaining <= 0) {
                    finish()
                    break
                }
                _state.update { it.copy(remainingMillis = remaining) }
                delay(100)
            }
        }
    }

    private fun pause() {
        tickJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    private fun finish() {
        tickJob?.cancel()
        _state.update { it.copy(remainingMillis = 0, isRunning = false, isFinished = true) }
        vibrate()
    }

    fun reset() {
        tickJob?.cancel()
        targetEndTime = 0L
        _state.update {
            it.copy(
                remainingMillis = 0,
                totalMillis = 0,
                isRunning = false,
                isActive = false,
                isFinished = false
            )
        }
    }

    private fun sanitize(value: String, max: Int): String {
        val digits = value.filter { it.isDigit() }.take(2)
        val n = digits.toIntOrNull() ?: return "0"
        return n.coerceAtMost(max).toString()
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        val pattern = longArrayOf(0, 500, 250, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
