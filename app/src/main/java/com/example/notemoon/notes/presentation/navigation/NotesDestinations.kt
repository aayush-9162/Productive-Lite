package com.example.notemoon.notes.presentation.navigation

/** Navigation argument key for the note id passed to the Add/Edit screen. */
const val NOTE_ID_ARG = "noteId"

/** Sentinel value meaning "no note" — i.e. the Add/Edit screen is in create mode. */
const val NO_NOTE_ID = -1L

/** Type-safe-ish route definitions for the Notes module. */
object NotesDestinations {

    /** The notes list screen. */
    const val NOTES_LIST = "notes_list"

    /** Base route for the Add/Edit screen; takes an optional [NOTE_ID_ARG]. */
    const val ADD_EDIT_NOTE = "add_edit_note"

    /** Full route pattern registered with the NavHost. */
    const val ADD_EDIT_NOTE_ROUTE = "$ADD_EDIT_NOTE?$NOTE_ID_ARG={$NOTE_ID_ARG}"

    /** Builds the Add/Edit route for creating a new note. */
    fun addNote(): String = ADD_EDIT_NOTE

    /** Builds the Add/Edit route for editing an existing note. */
    fun editNote(noteId: Long): String = "$ADD_EDIT_NOTE?$NOTE_ID_ARG=$noteId"
}
