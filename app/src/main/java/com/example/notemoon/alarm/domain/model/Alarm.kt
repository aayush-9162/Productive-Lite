package com.example.notemoon.alarm.domain.model

/**
 * A user alarm. [repeatDays] holds [java.util.Calendar] day-of-week values
 * (SUNDAY = 1 .. SATURDAY = 7); an empty set means the alarm rings once and then
 * disables itself. [soundUri] is null for the system default alarm sound.
 */
data class Alarm(
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val enabled: Boolean = true,
    val repeatDays: Set<Int> = emptySet(),
    val vibrate: Boolean = true,
    val soundUri: String? = null,
    val snoozeMinutes: Int = 10,
    /** When true, the user must solve [mathQuestions] +/- problems to dismiss. */
    val mathToDismiss: Boolean = false,
    val mathQuestions: Int = 2,
    val createdAt: Long = 0L
)
