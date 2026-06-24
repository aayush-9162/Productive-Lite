package com.example.notemoon.notes.presentation.addedit

/** Auto-save status surfaced to the Add/Edit screen. */
enum class SaveState { Idle, Saving, Saved }

/**
 * UI state for the Add/Edit screen. When [noteId] is null the screen is creating
 * a new note; otherwise it is editing the note with that id.
 *
 * [content] holds the note body as HTML (rich text). [isLoaded] flips to true
 * once an existing note has been read from the database (or immediately for a
 * new note) so the editor only initialises its content once.
 */
data class AddEditNoteState(
    val noteId: Long? = null,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long? = null,
    val isLoaded: Boolean = false,
    val saveState: SaveState = SaveState.Idle
)

/** One-off events emitted by the Add/Edit ViewModel for the screen to react to. */
sealed interface AddEditUiEvent {
    data class ShowError(val message: String) : AddEditUiEvent
}
