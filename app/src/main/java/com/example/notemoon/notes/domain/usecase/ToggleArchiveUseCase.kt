package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository

/**
 * Archives an active note or restores an archived one by flipping
 * [Note.isArchived]. This single use case covers both the "Archive" and the
 * "Restore Archived" requirements.
 */
class ToggleArchiveUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.upsertNote(note.copy(isArchived = !note.isArchived))
    }
}
