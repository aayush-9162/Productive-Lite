package com.example.notemoon.notes.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notemoon.notes.presentation.addedit.AddEditNoteScreen
import com.example.notemoon.notes.presentation.notes.NotesScreen

/**
 * Registers the Notes module's destinations (list + add/edit) on a shared
 * [NavController]. Called from the app-level nav host.
 */
fun NavGraphBuilder.notesGraph(navController: NavController) {
    composable(route = NotesDestinations.NOTES_LIST) {
        NotesScreen(
            onAddNote = { navController.navigate(NotesDestinations.addNote()) },
            onEditNote = { noteId -> navController.navigate(NotesDestinations.editNote(noteId)) }
        )
    }

    composable(
        route = NotesDestinations.ADD_EDIT_NOTE_ROUTE,
        arguments = listOf(
            navArgument(NOTE_ID_ARG) {
                type = NavType.LongType
                defaultValue = NO_NOTE_ID
            }
        )
    ) {
        AddEditNoteScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
