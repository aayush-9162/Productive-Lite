package com.example.notemoon.notes.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.usecase.NoteUseCases
import com.example.notemoon.notes.domain.util.NoteOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the notes list screen. Holds the [NotesState] and exposes intent-style
 * functions for every list action: search, sort, pin, favorite, archive/restore
 * and delete (with undo).
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    /** The most recently deleted note, kept so the user can undo the deletion. */
    private var recentlyDeletedNote: Note? = null

    /** Collection job for the current notes stream; cancelled and restarted when
     *  the search query, sort order or archive filter changes. */
    private var getNotesJob: Job? = null

    init {
        observeNotes()
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        observeNotes()
    }

    fun onOrderChange(noteOrder: NoteOrder) {
        _state.update { it.copy(noteOrder = noteOrder) }
        observeNotes()
    }

    fun toggleOrderMenu() {
        _state.update { it.copy(isOrderMenuVisible = !it.isOrderMenuVisible) }
    }

    fun setOrderMenuVisible(visible: Boolean) {
        _state.update { it.copy(isOrderMenuVisible = visible) }
    }

    fun setShowArchived(showArchived: Boolean) {
        _state.update { it.copy(showArchived = showArchived) }
        observeNotes()
    }

    fun togglePin(note: Note) {
        viewModelScope.launch { noteUseCases.togglePin(note) }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch { noteUseCases.toggleFavorite(note) }
    }

    fun toggleArchive(note: Note) {
        viewModelScope.launch { noteUseCases.toggleArchive(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteUseCases.deleteNote(note)
            recentlyDeletedNote = note
        }
    }

    fun restoreNote() {
        viewModelScope.launch {
            recentlyDeletedNote?.let { note -> noteUseCases.addNote(note) }
            recentlyDeletedNote = null
        }
    }

    private fun observeNotes() {
        val current = _state.value
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(
            noteOrder = current.noteOrder,
            archived = current.showArchived,
            query = current.searchQuery
        )
            .onEach { notes ->
                _state.update { it.copy(notes = notes, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }
}
