package com.example.notemoon.tasks.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notemoon.tasks.presentation.addedit.AddEditTaskScreen
import com.example.notemoon.tasks.presentation.details.TaskDetailsScreen
import com.example.notemoon.tasks.presentation.tasks.TasksScreen

/**
 * Registers the Tasks module's destinations (list + add/edit + details) on a
 * shared [NavController].
 */
fun NavGraphBuilder.tasksGraph(navController: NavController) {
    composable(route = TasksDestinations.TASKS_LIST) {
        TasksScreen(
            onAddTask = { navController.navigate(TasksDestinations.addTask()) },
            onOpenTask = { taskId -> navController.navigate(TasksDestinations.taskDetails(taskId)) }
        )
    }

    composable(
        route = TasksDestinations.ADD_EDIT_TASK_ROUTE,
        arguments = listOf(
            navArgument(TASK_ID_ARG) {
                type = NavType.LongType
                defaultValue = NO_TASK_ID
            }
        )
    ) {
        AddEditTaskScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = TasksDestinations.TASK_DETAILS_ROUTE,
        arguments = listOf(
            navArgument(TASK_ID_ARG) { type = NavType.LongType }
        )
    ) {
        TaskDetailsScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditTask = { taskId -> navController.navigate(TasksDestinations.editTask(taskId)) }
        )
    }
}
