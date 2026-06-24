package com.example.notemoon.tasks.domain.util

import com.example.notemoon.tasks.domain.model.RepeatType
import java.util.Calendar
import java.util.TimeZone

/**
 * Pure date/time helpers shared by the reminder scheduler (data layer) and the
 * UI (presentation layer).
 *
 * A task's date and time are stored separately:
 *  - dueDate: the picked day as UTC-midnight epoch millis (Material date picker
 *    reports the selection in UTC), or 0 when no due date is set.
 *  - dueTime: minutes since midnight of the picked time (hour * 60 + minute).
 */

/** Packs an hour-of-day and minute into the stored "minutes since midnight" value. */
fun packDueTime(hour: Int, minute: Int): Long = (hour * 60 + minute).toLong()

/** The hour-of-day component (0-23) of a stored dueTime. */
fun dueTimeHour(dueTime: Long): Int = ((dueTime / 60) % 24).toInt()

/** The minute component (0-59) of a stored dueTime. */
fun dueTimeMinute(dueTime: Long): Int = (dueTime % 60).toInt()

/**
 * Combines a UTC-midnight [dueDate] and a minutes-of-day [dueTime] into a single
 * epoch-millis timestamp in the device's local time zone — the moment a reminder
 * should fire. Returns 0 when there is no due date.
 */
fun combineDateAndTime(dueDate: Long, dueTime: Long): Long {
    if (dueDate <= 0L) return 0L

    // Extract the calendar day from the UTC-based date value.
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = dueDate
    }
    val year = utc.get(Calendar.YEAR)
    val month = utc.get(Calendar.MONTH)
    val day = utc.get(Calendar.DAY_OF_MONTH)

    // Rebuild that day with the chosen time in the local time zone.
    val local = Calendar.getInstance().apply {
        clear()
        set(year, month, day, dueTimeHour(dueTime), dueTimeMinute(dueTime), 0)
        set(Calendar.MILLISECOND, 0)
    }
    return local.timeInMillis
}

/**
 * Advances a UTC-midnight [dueDate] by one [RepeatType] interval, used to compute
 * the next occurrence of a recurring task. Returns the same value for
 * [com.example.notemoon.tasks.domain.model.RepeatType.NONE].
 */
fun nextOccurrence(dueDate: Long, repeatField: Int, repeatAmount: Int): Long {
    if (dueDate <= 0L || repeatAmount == 0) return dueDate
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = dueDate
        add(repeatField, repeatAmount)
    }
    return utc.timeInMillis
}

/** The next due date for a recurring task, advancing by one [repeatType] interval. */
fun advanceDueDate(dueDate: Long, repeatType: RepeatType): Long = when (repeatType) {
    RepeatType.NONE -> dueDate
    RepeatType.DAILY -> nextOccurrence(dueDate, Calendar.DAY_OF_MONTH, 1)
    RepeatType.WEEKLY -> nextOccurrence(dueDate, Calendar.DAY_OF_MONTH, 7)
    RepeatType.MONTHLY -> nextOccurrence(dueDate, Calendar.MONTH, 1)
}
