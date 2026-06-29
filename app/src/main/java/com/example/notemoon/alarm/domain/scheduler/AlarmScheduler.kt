package com.example.notemoon.alarm.domain.scheduler

import com.example.notemoon.alarm.domain.model.Alarm

/**
 * Schedules user alarms with the OS. Implemented with AlarmManager's
 * `setAlarmClock`, the dedicated user-alarm API that fires even in Doze and does
 * not require the exact-alarm permission.
 */
interface AlarmScheduler {

    /** Schedules the next occurrence of [alarm], or cancels it when disabled. */
    fun schedule(alarm: Alarm)

    /** Cancels any pending fire (and snooze) for the alarm with [alarmId]. */
    fun cancel(alarmId: Long)

    /** Schedules a one-off snooze for [alarm] at now + its snooze minutes. */
    fun snooze(alarm: Alarm)

    /** Cancels just the pending snooze for [alarmId], leaving its schedule intact. */
    fun cancelSnooze(alarmId: Long)

    /** Reschedules every enabled alarm, e.g. after a reboot or app update. */
    fun rescheduleAll(alarms: List<Alarm>)
}
