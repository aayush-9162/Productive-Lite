package com.example.notemoon.notes.domain.repository

import com.example.notemoon.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the notes data source. The domain layer depends only on this
 * interface; the concrete Room-backed implementation lives in the data layer.
 */
interface NoteRepository {

    /**
     * Streams notes filtered by archive state.
     *
     * @param archived when true returns archived notes, otherwise active notes.
     */
    fun getNotes(archived: Boolean): Flow<List<Note>>

    /** Returns a single note by id, or null if it does not exist. */
    suspend fun getNoteById(id: Long): Note?

    /**
     * Inserts a new note or updates an existing one (matched by id).
     * Returns the note's id (the generated id when inserting a new note).
     */
    suspend fun upsertNote(note: Note): Long

    /** Permanently deletes a note. */
    suspend fun deleteNote(note: Note)
}
