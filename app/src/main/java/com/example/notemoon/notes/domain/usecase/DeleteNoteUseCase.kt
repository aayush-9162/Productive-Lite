package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository

/** Permanently deletes a note. */
class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}
