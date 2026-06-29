package com.example.notemoon.alarm.domain.repository

import com.example.notemoon.alarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

/** Persistence operations for alarms. */
interface AlarmRepository {

    /** All alarms, ordered by time of day. */
    fun getAlarms(): Flow<List<Alarm>>

    /** A single alarm by id, or null if it no longer exists. */
    suspend fun getAlarmById(id: Long): Alarm?

    /** All alarms as a one-shot list (used when rescheduling after boot). */
    suspend fun getAlarmsList(): List<Alarm>

    /** Inserts or updates an alarm; returns its row id. */
    suspend fun upsertAlarm(alarm: Alarm): Long

    /** Flips the enabled flag for one alarm. */
    suspend fun setEnabled(id: Long, enabled: Boolean)

    /** Deletes an alarm. */
    suspend fun deleteAlarm(alarm: Alarm)
}
