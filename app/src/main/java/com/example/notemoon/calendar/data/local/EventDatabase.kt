package com.example.notemoon.calendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Room database hosting the events table — separate from the notes and tasks
 *  databases, keeping the feature modules independent. */
@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        const val DATABASE_NAME = "notemoon_calendar.db"
    }
}
