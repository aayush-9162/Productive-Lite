package com.example.notemoon.calendar.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Pure date/time helpers for the Calendar module. Days are represented as
 * UTC-midnight epoch millis (matching the Material date picker and the Tasks
 * module), and times of day as minutes since midnight.
 */
object CalendarDateUtils {

    private fun utc(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    private fun utcAt(millis: Long): Calendar = utc().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    /** Normalises any timestamp to the UTC-midnight of its day. */
    fun startOfDay(millis: Long): Long = utcAt(millis).timeInMillis

    /** Today as UTC-midnight, derived from the device's local calendar day. */
    fun today(): Long {
        val local = Calendar.getInstance()
        return buildDay(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun buildDay(year: Int, month: Int, day: Int): Long {
        val c = utc().apply {
            clear()
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    fun isSameDay(a: Long, b: Long): Boolean = startOfDay(a) == startOfDay(b)

    fun addDays(dayMillis: Long, days: Int): Long =
        utcAt(dayMillis).apply { add(Calendar.DAY_OF_MONTH, days) }.timeInMillis

    fun addMonths(monthAnchor: Long, months: Int): Long =
        utcAt(monthAnchor).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, months)
        }.timeInMillis

    fun firstOfMonth(monthAnchor: Long): Long =
        utcAt(monthAnchor).apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis

    /** The 42 days (6 weeks, Sunday-first) covering the month of [monthAnchor]. */
    fun monthGridDays(monthAnchor: Long): List<Long> {
        val c = utcAt(monthAnchor).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val offset = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY // 0..6
        c.add(Calendar.DAY_OF_MONTH, -offset)
        return List(42) {
            val d = c.timeInMillis
            c.add(Calendar.DAY_OF_MONTH, 1)
            d
        }
    }

    /** The 7 days (Sunday-first) of the week containing [dayMillis]. */
    fun weekDays(dayMillis: Long): List<Long> {
        val c = utcAt(dayMillis)
        val offset = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        c.add(Calendar.DAY_OF_MONTH, -offset)
        return List(7) {
            val d = c.timeInMillis
            c.add(Calendar.DAY_OF_MONTH, 1)
            d
        }
    }

    fun isInMonth(dayMillis: Long, monthAnchor: Long): Boolean {
        val a = utcAt(dayMillis)
        val b = utcAt(monthAnchor)
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
    }

    fun dayOfMonth(dayMillis: Long): Int = utcAt(dayMillis).get(Calendar.DAY_OF_MONTH)

    /** Short weekday headers, Sunday-first. */
    val weekdayHeaders: List<String> = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // ---- time-of-day (minutes since midnight) ----

    fun packTime(hour: Int, minute: Int): Long = (hour * 60 + minute).toLong()
    fun hourOf(minutes: Long): Int = ((minutes / 60) % 24).toInt()
    fun minuteOf(minutes: Long): Int = (minutes % 60).toInt()

    /** Combines a UTC-midnight day and minutes-of-day into a local epoch millis. */
    fun combine(dayMillis: Long, minutesOfDay: Long): Long {
        val u = utcAt(dayMillis)
        val local = Calendar.getInstance().apply {
            clear()
            set(
                u.get(Calendar.YEAR), u.get(Calendar.MONTH), u.get(Calendar.DAY_OF_MONTH),
                hourOf(minutesOfDay), minuteOf(minutesOfDay), 0
            )
            set(Calendar.MILLISECOND, 0)
        }
        return local.timeInMillis
    }

    // ---- formatting (UTC so the picked day never shifts) ----

    private fun fmt(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }

    fun monthTitle(monthAnchor: Long): String = fmt("MMMM yyyy").format(monthAnchor)
    fun fullDate(dayMillis: Long): String = fmt("EEE, dd MMM yyyy").format(dayMillis)
    fun shortDate(dayMillis: Long): String = fmt("dd MMM").format(dayMillis)
    fun weekdayName(dayMillis: Long): String = fmt("EEE").format(dayMillis)

    fun formatTime(minutesOfDay: Long): String =
        String.format(Locale.getDefault(), "%02d:%02d", hourOf(minutesOfDay), minuteOf(minutesOfDay))

    fun timeRange(start: Long, end: Long): String = "${formatTime(start)} – ${formatTime(end)}"
}
