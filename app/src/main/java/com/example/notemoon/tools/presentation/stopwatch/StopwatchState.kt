package com.example.notemoon.tools.presentation.stopwatch

/** UI state for the stopwatch. */
data class StopwatchState(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Long> = emptyList()
)
