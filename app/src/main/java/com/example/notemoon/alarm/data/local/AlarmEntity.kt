package com.example.notemoon.alarm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table for alarms. [repeatDays] is a comma-separated list of Calendar
 * day-of-week ints (empty string = one-time alarm).
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val label: String,
    val enabled: Boolean,
    val repeatDays: String,
    val vibrate: Boolean,
    val soundUri: String?,
    val snoozeMinutes: Int,
    val mathToDismiss: Boolean,
    val mathQuestions: Int,
    val createdAt: Long
)
