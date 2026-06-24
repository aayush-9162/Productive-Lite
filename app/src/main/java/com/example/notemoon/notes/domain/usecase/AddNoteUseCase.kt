package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.InvalidNoteException
import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository

/**
 * Creates or updates a note. Validates that the note is not completely empty
 * before persisting it.
 */
class AddNoteUseCase(
    private val repository: NoteRepository
) {
    /** Returns the saved note's id (the generated id for a new note). */
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note): Long {
        if (note.title.isBlank() && note.content.isBlank()) {
            throw InvalidNoteException("A note must have a title or some content.")
        }
        return repository.upsertNote(note)
    }
}
