package com.example.notemoon.tasks.presentation.tasks

import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.model.TaskStatistics
import com.example.notemoon.tasks.domain.util.TaskFilter
import com.example.notemoon.tasks.domain.util.TaskSort

/** Immutable UI state for the Tasks list screen. */
data class TasksState(
    val tasks: List<Task> = emptyList(),
    val statistics: TaskStatistics = TaskStatistics(),
    val searchQuery: String = "",
    val filter: TaskFilter = TaskFilter(),
    val sort: TaskSort = TaskSort(),
    val isFilterSheetVisible: Boolean = false,
    val isSortMenuVisible: Boolean = false,
    val isLoading: Boolean = true
)
