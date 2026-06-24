package com.example.notemoon.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.notemoon.calendar.presentation.navigation.CalendarDestinations
import com.example.notemoon.home.presentation.navigation.HOME_ROUTE
import com.example.notemoon.notes.presentation.navigation.NotesDestinations
import com.example.notemoon.tasks.presentation.navigation.TasksDestinations
import com.example.notemoon.tools.presentation.navigation.ToolsDestinations

/** The bottom-navigation entries, one per feature area. */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME(HOME_ROUTE, "Home", Icons.Filled.Home),
    NOTES(NotesDestinations.NOTES_LIST, "Notes", Icons.Filled.Description),
    CALENDAR(CalendarDestinations.CALENDAR, "Calendar", Icons.Filled.DateRange),
    TASKS(TasksDestinations.TASKS_LIST, "Tasks", Icons.Filled.TaskAlt),
    TOOLS(ToolsDestinations.TOOLS, "Tools", Icons.Filled.Calculate)
}
