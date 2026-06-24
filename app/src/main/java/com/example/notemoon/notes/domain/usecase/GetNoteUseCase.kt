package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository

/** Loads a single note by id, used by the Add/Edit screen when editing. */
class GetNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Note? {
        return repository.getNoteById(id)
    }
}
