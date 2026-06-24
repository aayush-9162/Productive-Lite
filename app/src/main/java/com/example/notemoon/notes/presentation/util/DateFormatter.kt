package com.example.notemoon.notes.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats an epoch-millis timestamp into a short human-readable date/time, e.g.
 * "23 Jun 2026, 14:05". Uses [SimpleDateFormat] so it works on the module's
 * minSdk (24) without core-library desugaring.
 */
fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}

/**
 * Returns a short, friendly relative time such as "Just now", "5 min ago",
 * "3 h ago" or "Yesterday", falling back to an absolute date for older notes.
 */
fun relativeTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis
    return when {
        diff < 0 -> formatTimestamp(millis)
        diff < 60_000L -> "Just now"
        diff < 3_600_000L -> "${diff / 60_000L} min ago"
        diff < 86_400_000L -> "${diff / 3_600_000L} h ago"
        diff < 172_800_000L -> "Yesterday"
        diff < 604_800_000L -> "${diff / 86_400_000L} days ago"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
    }
}
