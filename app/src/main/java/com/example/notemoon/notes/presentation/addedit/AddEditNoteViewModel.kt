package com.example.notemoon.notes.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.notes.di.ApplicationScope
import com.example.notemoon.notes.domain.model.InvalidNoteException
import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.usecase.NoteUseCases
import com.example.notemoon.notes.domain.util.stripHtml
import com.example.notemoon.notes.presentation.navigation.NO_NOTE_ID
import com.example.notemoon.notes.presentation.navigation.NOTE_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Add/Edit screen with **auto-save**. There is no Save button: every
 * change to the title, body or flags is persisted automatically after a short
 * debounce, and a final [flush] runs when the screen is disposed so the last
 * keystrokes are never lost (it uses an application-scoped coroutine that
 * outlives this ViewModel).
 */
@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditNoteState())
    val state: StateFlow<AddEditNoteState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddEditUiEvent>()
    val eventFlow: SharedFlow<AddEditUiEvent> = _eventFlow.asSharedFlow()

    private var autoSaveJob: Job? = null

    // Snapshot of what is currently persisted, used to skip redundant writes.
    private var lastSavedTitle: String = ""
    private var lastSavedContent: String = ""

    init {
        val noteId = savedStateHandle.get<Long>(NOTE_ID_ARG) ?: NO_NOTE_ID
        if (noteId != NO_NOTE_ID) {
            viewModelScope.launch {
                val note = noteUseCases.getNote(noteId)
                if (note != null) {
                    lastSavedTitle = note.title
                    lastSavedContent = note.content
                    _state.update {
                        it.copy(
                            noteId = note.id,
                            title = note.title,
                            content = note.content,
                            isPinned = note.isPinned,
                            isFavorite = note.isFavorite,
                            isArchived = note.isArchived,
                            createdAt = note.createdAt,
                            isLoaded = true,
                            saveState = SaveState.Saved
                        )
                    }
                } else {
                    _state.update { it.copy(isLoaded = true) }
                }
            }
        } else {
            // New note: nothing to load.
            _state.update { it.copy(isLoaded = true) }
        }
    }

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title, saveState = SaveState.Idle) }
        scheduleAutoSave()
    }

    /** [html] is the rich-text body serialised to HTML by the editor. */
    fun onContentChange(html: String) {
        _state.update { it.copy(content = html, saveState = SaveState.Idle) }
        scheduleAutoSave()
    }

    fun onTogglePin() {
        _state.update { it.copy(isPinned = !it.isPinned) }
        scheduleAutoSave()
    }

    fun onToggleFavorite() {
        _state.update { it.copy(isFavorite = !it.isFavorite) }
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(AUTO_SAVE_DELAY_MS)
            persist()
        }
    }

    /** Cancels any pending debounce and saves immediately on the application
     *  scope so the write completes even after this ViewModel is cleared. */
    fun flush() {
        autoSaveJob?.cancel()
        applicationScope.launch { persist() }
    }

    private suspend fun persist() {
        val current = _state.value

        // Don't create or keep saving an entirely empty note.
        if (current.title.isBlank() && current.content.stripHtml().isBlank()) {
            _state.update { it.copy(saveState = SaveState.Idle) }
            return
        }
        // Nothing changed since the last save.
        if (current.title == lastSavedTitle && current.content == lastSavedContent) {
            _state.update { it.copy(saveState = SaveState.Saved) }
            return
        }

        _state.update { it.copy(saveState = SaveState.Saving) }
        val now = System.currentTimeMillis()
        val note = Note(
            id = current.noteId ?: 0L,
            title = current.title.trim(),
            content = current.content,
            isPinned = current.isPinned,
            isFavorite = current.isFavorite,
            isArchived = current.isArchived,
            createdAt = current.createdAt ?: now,
            updatedAt = now
        )

        try {
            val savedId = noteUseCases.addNote(note)
            lastSavedTitle = current.title
            lastSavedContent = current.content
            _state.update {
                it.copy(
                    noteId = if (it.noteId == null && savedId > 0L) savedId else it.noteId,
                    createdAt = it.createdAt ?: now,
                    saveState = SaveState.Saved
                )
            }
        } catch (e: InvalidNoteException) {
            _eventFlow.emit(
                AddEditUiEvent.ShowError(e.message ?: "Could not save the note.")
            )
        }
    }

    companion object {
        private const val AUTO_SAVE_DELAY_MS = 600L
    }
}
