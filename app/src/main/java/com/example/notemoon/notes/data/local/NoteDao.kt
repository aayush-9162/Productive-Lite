package com.example.notemoon.notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Room data-access object for [NoteEntity]. */
@Dao
interface NoteDao {

    /** Active (non-archived) notes as a reactive stream. */
    @Query("SELECT * FROM notes WHERE isArchived = 0")
    fun getActiveNotes(): Flow<List<NoteEntity>>

    /** Archived notes as a reactive stream. */
    @Query("SELECT * FROM notes WHERE isArchived = 1")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    /** A single note by id, or null if not found. */
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    /** All notes as a one-shot list, used by backup/export. */
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesList(): List<NoteEntity>

    /**
     * Inserts a new note or updates an existing one (matched by primary key).
     * Returns the note's row id, which equals its [NoteEntity.id]. For a freshly
     * inserted note this is the newly generated id — used by auto-save to keep
     * editing the same row.
     */
    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    /** Permanently removes a note. */
    @Delete
    suspend fun deleteNote(note: NoteEntity)
}
