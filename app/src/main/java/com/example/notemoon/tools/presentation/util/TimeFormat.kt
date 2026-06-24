package com.example.notemoon.tools.presentation.util

import java.util.Locale

/** Formats stopwatch elapsed milliseconds as "MM:SS.cs" (or "H:MM:SS.cs"). */
fun formatStopwatch(elapsedMillis: Long): String {
    val totalSeconds = elapsedMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val centis = (elapsedMillis % 1000) / 10
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d.%02d", hours, minutes, seconds, centis)
    } else {
        String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centis)
    }
}

/** Formats a remaining-seconds count as "HH:MM:SS". */
fun formatTimer(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}
