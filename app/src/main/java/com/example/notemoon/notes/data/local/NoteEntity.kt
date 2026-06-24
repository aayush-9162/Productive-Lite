package com.example.notemoon.notes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table for notes. The column set mirrors the database fields required by
 * the Notes module:
 * id, title, content, isPinned, isFavorite, isArchived, createdAt, updatedAt.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
