package com.example.notemoon.notes.presentation.notes

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.util.NoteOrder
import com.example.notemoon.notes.domain.util.OrderType

/**
 * Immutable UI state for the notes list screen. The screen renders purely from
 * this object, which is the single source of truth exposed by [NotesViewModel].
 */
data class NotesState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val noteOrder: NoteOrder = NoteOrder.LastModified(OrderType.Descending),
    val isOrderMenuVisible: Boolean = false,
    val showArchived: Boolean = false,
    val isLoading: Boolean = true
)
