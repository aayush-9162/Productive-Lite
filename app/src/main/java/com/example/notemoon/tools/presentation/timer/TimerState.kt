package com.example.notemoon.tools.presentation.timer

/** UI state for the countdown timer. */
data class TimerState(
    val inputHours: String = "0",
    val inputMinutes: String = "5",
    val inputSeconds: String = "0",
    val remainingMillis: Long = 0L,
    val totalMillis: Long = 0L,
    val isRunning: Boolean = false,
    val isActive: Boolean = false,
    val isFinished: Boolean = false
) {
    /** Countdown progress from 1 (full) to 0 (done). */
    val progress: Float
        get() = if (totalMillis <= 0) 0f else (remainingMillis.toFloat() / totalMillis).coerceIn(0f, 1f)
}
