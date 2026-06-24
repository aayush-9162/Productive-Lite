package com.example.notemoon.notes.domain.usecase

import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.repository.NoteRepository
import com.example.notemoon.notes.domain.util.NoteOrder
import com.example.notemoon.notes.domain.util.OrderType
import com.example.notemoon.notes.domain.util.stripHtml
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Streams the notes for a list screen, applying (in order):
 *  1. archive filtering (active vs. archived),
 *  2. a free-text search over title and content,
 *  3. the requested sort field/direction,
 *  4. pinned notes always floated to the top.
 *
 * Searching and sorting are done in memory so the underlying [Flow] stays a
 * single reactive source of truth — the list updates automatically whenever the
 * database, the query or the sort order changes.
 */
class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(
        noteOrder: NoteOrder,
        archived: Boolean = false,
        query: String = ""
    ): Flow<List<Note>> {
        return repository.getNotes(archived).map { notes ->
            val filtered = if (query.isBlank()) {
                notes
            } else {
                notes.filter { note ->
                    note.title.contains(query, ignoreCase = true) ||
                        note.content.stripHtml().contains(query, ignoreCase = true)
                }
            }

            val sorted = when (noteOrder.orderType) {
                OrderType.Ascending -> when (noteOrder) {
                    is NoteOrder.Title -> filtered.sortedBy { it.title.lowercase() }
                    is NoteOrder.DateCreated -> filtered.sortedBy { it.createdAt }
                    is NoteOrder.LastModified -> filtered.sortedBy { it.updatedAt }
                }

                OrderType.Descending -> when (noteOrder) {
                    is NoteOrder.Title -> filtered.sortedByDescending { it.title.lowercase() }
                    is NoteOrder.DateCreated -> filtered.sortedByDescending { it.createdAt }
                    is NoteOrder.LastModified -> filtered.sortedByDescending { it.updatedAt }
                }
            }

            // Pinned notes always come first, preserving the sort within each group.
            sorted.sortedByDescending { it.isPinned }
        }
    }
}
