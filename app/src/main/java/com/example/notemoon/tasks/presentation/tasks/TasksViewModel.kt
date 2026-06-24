package com.example.notemoon.tasks.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import com.example.notemoon.tasks.domain.util.TaskFilter
import com.example.notemoon.tasks.domain.util.TaskSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Tasks list screen: search, filter, sort, live statistics, toggling
 * completion, and delete-with-undo. Completion and deletion also update the
 * task's reminder via the [TaskReminderScheduler].
 */
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val reminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    private var recentlyDeletedTask: Task? = null
    private var getTasksJob: Job? = null

    init {
        observeTasks()
        observeStatistics()
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        observeTasks()
    }

    fun onFilterChange(filter: TaskFilter) {
        _state.update { it.copy(filter = filter) }
        observeTasks()
    }

    fun onSortChange(sort: TaskSort) {
        _state.update { it.copy(sort = sort) }
        observeTasks()
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _state.update { it.copy(isFilterSheetVisible = visible) }
    }

    fun setSortMenuVisible(visible: Boolean) {
        _state.update { it.copy(isSortMenuVisible = visible) }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            val updated = taskUseCases.toggleCompletion(task)
            // Completing cancels the reminder; un-completing reinstates it.
            reminderScheduler.schedule(updated)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            reminderScheduler.cancel(task.id)
            recentlyDeletedTask = task
        }
    }

    fun restoreTask() {
        viewModelScope.launch {
            recentlyDeletedTask?.let { task ->
                taskUseCases.addTask(task)
                reminderScheduler.schedule(task)
            }
            recentlyDeletedTask = null
        }
    }

    private fun observeTasks() {
        val current = _state.value
        getTasksJob?.cancel()
        getTasksJob = taskUseCases.getTasks(
            query = current.searchQuery,
            filter = current.filter,
            sort = current.sort
        )
            .onEach { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeStatistics() {
        taskUseCases.getStatistics()
            .onEach { stats -> _state.update { it.copy(statistics = stats) } }
            .launchIn(viewModelScope)
    }
}
