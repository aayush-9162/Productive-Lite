package com.example.notemoon.settings.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.notemoon.settings.presentation.SettingsScreen

object SettingsDestinations {
    const val SETTINGS = "settings"
}

/** Registers the Settings screen (a full-screen route, not a bottom-nav tab). */
fun NavGraphBuilder.settingsGraph(navController: NavController) {
    composable(route = SettingsDestinations.SETTINGS) {
        SettingsScreen(onNavigateBack = { navController.popBackStack() })
    }
}
