package com.example.notemoon.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notemoon.calendar.presentation.navigation.calendarGraph
import com.example.notemoon.home.presentation.navigation.HOME_ROUTE
import com.example.notemoon.home.presentation.navigation.homeGraph
import com.example.notemoon.notes.presentation.navigation.notesGraph
import com.example.notemoon.settings.presentation.navigation.settingsGraph
import com.example.notemoon.tasks.presentation.navigation.TasksDestinations
import com.example.notemoon.tasks.presentation.navigation.tasksGraph
import com.example.notemoon.tools.presentation.navigation.toolsGraph

/**
 * App shell: a bottom navigation bar (Home, Calendar, Tasks, Notes) hosting a
 * single [NavHost] that combines every feature module's graph. The bar shows
 * only on the four top-level screens; full-screen editor/detail routes hide it.
 *
 * [deepLinkTaskId] (set when opened from a reminder notification) navigates to
 * that task's details, then [onDeepLinkHandled] clears it.
 */
@Composable
fun MainScreen(
    deepLinkTaskId: Long?,
    onDeepLinkHandled: () -> Unit
) {
    val navController = rememberNavController()
    val topLevelRoutes = TopLevelDestination.entries.map { it.route }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.hierarchy?.any { it.route in topLevelRoutes } == true

    // Standard bottom-nav behaviour: single top, save/restore each tab's state.
    val tabNavOptions: NavOptionsBuilder.() -> Unit = {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    LaunchedEffect(deepLinkTaskId) {
        deepLinkTaskId?.let { taskId ->
            navController.navigate(TasksDestinations.taskDetails(taskId))
            onDeepLinkHandled()
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigate(destination.route, tabNavOptions) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME_ROUTE,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            homeGraph(navController, onSelectCalendarTab = tabNavOptions)
            calendarGraph(navController)
            tasksGraph(navController)
            notesGraph(navController)
            toolsGraph(navController)
            settingsGraph(navController)
        }
    }
}
