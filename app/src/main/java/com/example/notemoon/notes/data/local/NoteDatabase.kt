package com.example.notemoon.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** The Room database hosting the notes table. */
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "notemoon.db"
    }
}
