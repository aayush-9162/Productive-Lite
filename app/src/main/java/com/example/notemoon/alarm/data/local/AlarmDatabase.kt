package com.example.notemoon.alarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Room database hosting the alarms table, separate from the other modules' DBs. */
@Database(
    entities = [AlarmEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        const val DATABASE_NAME = "notemoon_alarms.db"
    }
}
