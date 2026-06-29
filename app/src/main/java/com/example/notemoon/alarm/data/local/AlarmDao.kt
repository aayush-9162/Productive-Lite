package com.example.notemoon.alarm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Room data-access object for [AlarmEntity]. */
@Dao
interface AlarmDao {

    @Upsert
    suspend fun upsertAlarm(alarm: AlarmEntity): Long

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("UPDATE alarms SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Query("SELECT * FROM alarms")
    suspend fun getAlarmsList(): List<AlarmEntity>
}
