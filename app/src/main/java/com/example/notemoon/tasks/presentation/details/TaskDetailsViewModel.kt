package com.example.notemoon.tasks.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import com.example.notemoon.tasks.presentation.navigation.NO_TASK_ID
import com.example.notemoon.tasks.presentation.navigation.TASK_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailsState(
    val task: Task? = null,
    val isLoading: Boolean = true
)

/** Backs the Task Details screen: loads the task and supports toggling
 *  completion and deletion. */
@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val reminderScheduler: TaskReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailsState())
    val state: StateFlow<TaskDetailsState> = _state.asStateFlow()

    private val _navigateBack = Channel<Unit>()
    val navigateBack = _navigateBack.receiveAsFlow()

    private val taskId: Long = savedStateHandle.get<Long>(TASK_ID_ARG) ?: NO_TASK_ID

    init {
        viewModelScope.launch {
            val task = if (taskId == NO_TASK_ID) null else taskUseCases.getTask(taskId)
            _state.update { it.copy(task = task, isLoading = false) }
        }
    }

    fun toggleComplete() {
        val task = _state.value.task ?: return
        viewModelScope.launch {
            val updated = taskUseCases.toggleCompletion(task)
            reminderScheduler.schedule(updated)
            _state.update { it.copy(task = updated) }
        }
    }

    fun deleteTask() {
        val task = _state.value.task ?: return
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            reminderScheduler.cancel(task.id)
            _navigateBack.send(Unit)
        }
    }
}
