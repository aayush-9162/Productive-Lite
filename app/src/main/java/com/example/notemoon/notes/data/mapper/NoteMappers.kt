package com.example.notemoon.notes.data.mapper

import com.example.notemoon.notes.data.local.NoteEntity
import com.example.notemoon.notes.domain.model.Note

/** Maps a Room [NoteEntity] to its domain [Note]. */
fun NoteEntity.toNote(): Note = Note(
    id = id,
    title = title,
    content = content,
    isPinned = isPinned,
    isFavorite = isFavorite,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Maps a domain [Note] to its Room [NoteEntity]. */
fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    isPinned = isPinned,
    isFavorite = isFavorite,
    isArchived = isArchived,
    createdAt = createdAt,
    updatedAt = updatedAt
)
