package com.example.notemoon.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.example.notemoon.calendar.presentation.navigation.CalendarDestinations
import com.example.notemoon.home.presentation.HomeScreen
import com.example.notemoon.settings.presentation.navigation.SettingsDestinations
import com.example.notemoon.tasks.presentation.navigation.TasksDestinations

const val HOME_ROUTE = "home"

/**
 * Registers the Home dashboard. It links across modules: opening the Calendar
 * tab, an event's details, or a task's details via the shared [NavController].
 */
fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onSelectCalendarTab: NavOptionsBuilder.() -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            onOpenCalendar = {
                navController.navigate(CalendarDestinations.CALENDAR, onSelectCalendarTab)
            },
            onOpenEvent = { eventId ->
                navController.navigate(CalendarDestinations.eventDetails(eventId))
            },
            onOpenTask = { taskId ->
                navController.navigate(TasksDestinations.taskDetails(taskId))
            },
            onOpenSettings = {
                navController.navigate(SettingsDestinations.SETTINGS)
            }
        )
    }
}
