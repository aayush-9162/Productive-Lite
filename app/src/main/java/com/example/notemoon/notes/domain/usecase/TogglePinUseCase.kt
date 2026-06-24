package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository

/** Pins or unpins a note. Metadata-only change, so [Note.updatedAt] is untouched. */
class TogglePinUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.upsertNote(note.copy(isPinned = !note.isPinned))
    }
}
