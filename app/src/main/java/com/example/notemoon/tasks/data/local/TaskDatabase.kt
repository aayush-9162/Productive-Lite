package com.example.notemoon.tasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Room database hosting the tasks table. Separate from the notes database to
 *  keep the two feature modules independent. */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "notemoon_tasks.db"
    }
}
