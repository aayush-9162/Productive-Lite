package com.example.notemoon.tools.presentation.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Stopwatch with start/pause, lap and reset, ticking ~30ms while running. */
@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private var tickJob: Job? = null
    private var accumulated = 0L
    private var startedAt = 0L

    fun startOrPause() {
        if (_state.value.isRunning) pause() else start()
    }

    private fun start() {
        startedAt = System.currentTimeMillis()
        _state.update { it.copy(isRunning = true) }
        tickJob = viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _state.update { it.copy(elapsedMillis = accumulated + (now - startedAt)) }
                delay(30)
            }
        }
    }

    private fun pause() {
        tickJob?.cancel()
        accumulated += System.currentTimeMillis() - startedAt
        _state.update { it.copy(isRunning = false, elapsedMillis = accumulated) }
    }

    fun lap() {
        if (_state.value.elapsedMillis > 0) {
            _state.update { it.copy(laps = it.laps + it.elapsedMillis) }
        }
    }

    fun reset() {
        tickJob?.cancel()
        accumulated = 0L
        startedAt = 0L
        _state.update { StopwatchState() }
    }

    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }
}
