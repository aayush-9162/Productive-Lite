package com.example.notemoon.calendar.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notemoon.calendar.presentation.addedit.AddEditEventScreen
import com.example.notemoon.calendar.presentation.calendar.CalendarScreen
import com.example.notemoon.calendar.presentation.details.EventDetailsScreen
import com.example.notemoon.tasks.presentation.navigation.TasksDestinations

/**
 * Registers the Calendar module's destinations (calendar + add/edit event +
 * event details). Tapping a task on the calendar deep-links into the Tasks
 * module's details route via the shared [NavController].
 */
fun NavGraphBuilder.calendarGraph(navController: NavController) {
    composable(route = CalendarDestinations.CALENDAR) {
        CalendarScreen(
            onAddEvent = { date -> navController.navigate(CalendarDestinations.addEvent(date)) },
            onOpenEvent = { eventId -> navController.navigate(CalendarDestinations.eventDetails(eventId)) },
            onOpenTask = { taskId -> navController.navigate(TasksDestinations.taskDetails(taskId)) }
        )
    }

    composable(
        route = CalendarDestinations.ADD_EDIT_EVENT_ROUTE,
        arguments = listOf(
            navArgument(EVENT_ID_ARG) {
                type = NavType.LongType
                defaultValue = NO_EVENT_ID
            },
            navArgument(EVENT_DATE_ARG) {
                type = NavType.LongType
                defaultValue = NO_DATE
            }
        )
    ) {
        AddEditEventScreen(onNavigateBack = { navController.popBackStack() })
    }

    composable(
        route = CalendarDestinations.EVENT_DETAILS_ROUTE,
        arguments = listOf(navArgument(EVENT_ID_ARG) { type = NavType.LongType })
    ) {
        EventDetailsScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditEvent = { eventId -> navController.navigate(CalendarDestinations.editEvent(eventId)) }
        )
    }
}
