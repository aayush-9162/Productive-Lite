package com.example.notemoon.notes.domain.usecase

/**
 * Bundles every note use case into a single object that is injected into the
 * ViewModels. This keeps ViewModel constructors small and makes the available
 * note operations easy to discover.
 */
data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val getNote: GetNoteUseCase,
    val addNote: AddNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val togglePin: TogglePinUseCase,
    val toggleFavorite: ToggleFavoriteUseCase,
    val toggleArchive: ToggleArchiveUseCase
)
