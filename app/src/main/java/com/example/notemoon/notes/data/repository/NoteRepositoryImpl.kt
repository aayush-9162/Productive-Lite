package com.example.notemoon.notes.data.repository

import com.example.notemoon.notes.data.local.NoteDao
import com.example.notemoon.notes.data.mapper.toEntity
import com.example.notemoon.notes.data.mapper.toNote
import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Room-backed implementation of [NoteRepository]. It maps between the database
 * [com.example.notemoon.notes.data.local.NoteEntity] and the domain [Note] so
 * neither layer leaks into the other.
 */
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override fun getNotes(archived: Boolean): Flow<List<Note>> {
        val source = if (archived) dao.getArchivedNotes() else dao.getActiveNotes()
        return source.map { entities -> entities.map { it.toNote() } }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return dao.getNoteById(id)?.toNote()
    }

    override suspend fun upsertNote(note: Note): Long {
        return dao.upsertNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note.toEntity())
    }
}
