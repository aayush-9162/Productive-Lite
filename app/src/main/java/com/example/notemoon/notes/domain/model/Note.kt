package com.example.notemoon.notes.domain.model

/**
 * Domain representation of a single note.
 *
 * This is the model the presentation and domain layers work with. It is kept
 * separate from the Room [com.example.notemoon.notes.data.local.NoteEntity] so the
 * UI never depends on the database schema directly.
 */
data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

/** Thrown when a note fails validation (e.g. an empty title and content). */
class InvalidNoteException(message: String) : Exception(message)
