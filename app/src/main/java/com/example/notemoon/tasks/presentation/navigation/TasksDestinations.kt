package com.example.notemoon.tasks.presentation.navigation

/** Navigation argument key for the task id. */
const val TASK_ID_ARG = "taskId"

/** Sentinel meaning "no task" — the Add/Edit screen is in create mode. */
const val NO_TASK_ID = -1L

/** Route definitions for the Tasks module. */
object TasksDestinations {

    const val TASKS_LIST = "tasks_list"

    const val ADD_EDIT_TASK = "add_edit_task"
    const val ADD_EDIT_TASK_ROUTE = "$ADD_EDIT_TASK?$TASK_ID_ARG={$TASK_ID_ARG}"

    const val TASK_DETAILS = "task_details"
    const val TASK_DETAILS_ROUTE = "$TASK_DETAILS/{$TASK_ID_ARG}"

    fun addTask(): String = ADD_EDIT_TASK

    fun editTask(taskId: Long): String = "$ADD_EDIT_TASK?$TASK_ID_ARG=$taskId"

    fun taskDetails(taskId: Long): String = "$TASK_DETAILS/$taskId"
}
