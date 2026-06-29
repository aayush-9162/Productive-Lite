package com.example.notemoon.alarm.domain.util

import java.util.Calendar

/** The high-level repeat presets offered in the editor. */
enum class RepeatPreset { ONCE, DAILY, WEEKDAYS, WEEKENDS, CUSTOM }

/**
 * Pure helpers for alarm timing and repeat-day handling, shared by the scheduler
 * (data layer) and the UI (presentation layer). Days are [Calendar] day-of-week
 * constants (SUNDAY = 1 .. SATURDAY = 7).
 */
object AlarmSchedule {

    val WEEKDAYS: Set<Int> = setOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY
    )
    val WEEKENDS: Set<Int> = setOf(Calendar.SATURDAY, Calendar.SUNDAY)
    val EVERY_DAY: Set<Int> = setOf(
        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    )

    /** Days in display order (Mon..Sun) for rendering the day chips. */
    val ORDERED_DAYS: List<Int> = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
        Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    )

    fun presetOf(days: Set<Int>): RepeatPreset = when (days) {
        emptySet<Int>() -> RepeatPreset.ONCE
        EVERY_DAY -> RepeatPreset.DAILY
        WEEKDAYS -> RepeatPreset.WEEKDAYS
        WEEKENDS -> RepeatPreset.WEEKENDS
        else -> RepeatPreset.CUSTOM
    }

    fun daysForPreset(preset: RepeatPreset, current: Set<Int>): Set<Int> = when (preset) {
        RepeatPreset.ONCE -> emptySet()
        RepeatPreset.DAILY -> EVERY_DAY
        RepeatPreset.WEEKDAYS -> WEEKDAYS
        RepeatPreset.WEEKENDS -> WEEKENDS
        RepeatPreset.CUSTOM -> current.ifEmpty { EVERY_DAY }
    }

    /** Single-letter label for a day chip, e.g. M, T, W. */
    fun dayInitial(day: Int): String = when (day) {
        Calendar.MONDAY -> "M"
        Calendar.TUESDAY -> "T"
        Calendar.WEDNESDAY -> "W"
        Calendar.THURSDAY -> "T"
        Calendar.FRIDAY -> "F"
        Calendar.SATURDAY -> "S"
        else -> "S"
    }

    private fun dayShort(day: Int): String = when (day) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Sun"
    }

    /** Human summary of the repeat schedule, e.g. "Weekdays", "Mon, Wed, Fri". */
    fun repeatSummary(days: Set<Int>): String = when (presetOf(days)) {
        RepeatPreset.ONCE -> "Once"
        RepeatPreset.DAILY -> "Every day"
        RepeatPreset.WEEKDAYS -> "Weekdays"
        RepeatPreset.WEEKENDS -> "Weekends"
        RepeatPreset.CUSTOM -> ORDERED_DAYS.filter { it in days }.joinToString(", ") { dayShort(it) }
    }

    /** Formats an hour/minute as a 12-hour clock string, e.g. "7:05 AM". */
    fun formatTime(hour: Int, minute: Int): String {
        val h12 = when (hour % 12) { 0 -> 12; else -> hour % 12 }
        val ampm = if (hour < 12) "AM" else "PM"
        return "%d:%02d %s".format(h12, minute, ampm)
    }

    /**
     * The next epoch-millis at which an alarm with this time and repeat set should
     * fire, strictly after [fromMillis]. For a one-time alarm (empty [days]) this
     * is today at the given time if still ahead, otherwise tomorrow.
     */
    fun nextTriggerMillis(hour: Int, minute: Int, days: Set<Int>, fromMillis: Long): Long {
        val base = Calendar.getInstance().apply {
            timeInMillis = fromMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (days.isEmpty()) {
            if (base.timeInMillis <= fromMillis) base.add(Calendar.DAY_OF_YEAR, 1)
            return base.timeInMillis
        }
        for (offset in 0..7) {
            val candidate = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
            if (candidate.timeInMillis <= fromMillis) continue
            if (candidate.get(Calendar.DAY_OF_WEEK) in days) return candidate.timeInMillis
        }
        return base.timeInMillis
    }

    /** A short "rings in 7h 5m" style countdown for the given trigger time. */
    fun untilSummary(triggerMillis: Long, fromMillis: Long): String {
        val totalMin = ((triggerMillis - fromMillis) / 60_000L).coerceAtLeast(0L)
        val days = totalMin / (24 * 60)
        val hours = (totalMin % (24 * 60)) / 60
        val minutes = totalMin % 60
        return buildString {
            append("Rings in ")
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            append("${minutes}m")
        }
    }
}
