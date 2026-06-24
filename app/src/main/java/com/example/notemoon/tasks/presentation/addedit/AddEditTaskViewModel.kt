package com.example.notemoon.tasks.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.tasks.domain.model.InvalidTaskException
import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.RepeatType
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.model.TaskCategory
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import com.example.notemoon.tasks.domain.util.packDueTime
import com.example.notemoon.tasks.presentation.navigation.NO_TASK_ID
import com.example.notemoon.tasks.presentation.navigation.TASK_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Add/Edit Task screen: loads an existing task when editing, tracks
 * all editable fields, and on save persists the task and (re)schedules its
 * reminder via [TaskReminderScheduler].
 */
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val reminderScheduler: TaskReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditTaskState())
    val state: StateFlow<AddEditTaskState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddEditTaskUiEvent>()
    val eventFlow: SharedFlow<AddEditTaskUiEvent> = _eventFlow.asSharedFlow()

    init {
        val taskId = savedStateHandle.get<Long>(TASK_ID_ARG) ?: NO_TASK_ID
        if (taskId != NO_TASK_ID) {
            viewModelScope.launch {
                taskUseCases.getTask(taskId)?.let { task ->
                    _state.update {
                        it.copy(
                            taskId = task.id,
                            title = task.title,
                            description = task.description,
                            priority = task.priority,
                            category = task.category,
                            isCustomCategory = task.category !in TaskCategory.presets,
                            dueDate = task.dueDate,
                            dueTime = task.dueTime,
                            isCompleted = task.isCompleted,
                            reminderEnabled = task.reminderEnabled,
                            repeatType = task.repeatType,
                            createdAt = task.createdAt,
                            isLoaded = true
                        )
                    }
                } ?: _state.update { it.copy(isLoaded = true) }
            }
        } else {
            _state.update { it.copy(isLoaded = true) }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value) }

    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }

    fun onPriorityChange(value: Priority) = _state.update { it.copy(priority = value) }

    fun onPresetCategorySelected(category: String) =
        _state.update { it.copy(category = category, isCustomCategory = false) }

    fun onCustomCategorySelected() =
        _state.update { it.copy(isCustomCategory = true, category = "") }

    fun onCustomCategoryChange(value: String) = _state.update { it.copy(category = value) }

    fun onDueDateChange(dateUtcMillis: Long?) =
        _state.update { it.copy(dueDate = dateUtcMillis ?: 0L) }

    fun clearDueDate() =
        _state.update { it.copy(dueDate = 0L, reminderEnabled = false) }

    fun onDueTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(dueTime = packDueTime(hour, minute)) }

    fun onReminderToggle(enabled: Boolean) =
        _state.update { it.copy(reminderEnabled = enabled) }

    fun onRepeatChange(value: RepeatType) = _state.update { it.copy(repeatType = value) }

    fun saveTask() {
        val current = _state.value
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val task = Task(
                id = current.taskId ?: 0L,
                title = current.title.trim(),
                description = current.description.trim(),
                priority = current.priority,
                category = current.category.trim().ifBlank { TaskCategory.DEFAULT },
                dueDate = current.dueDate,
                dueTime = current.dueTime,
                isCompleted = current.isCompleted,
                // A reminder only makes sense when there is a due date.
                reminderEnabled = current.reminderEnabled && current.hasDueDate,
                repeatType = current.repeatType,
                createdAt = current.createdAt ?: now,
                updatedAt = now
            )

            try {
                val savedId = taskUseCases.addTask(task)
                val saved = task.copy(id = if (task.id == 0L) savedId else task.id)
                reminderScheduler.schedule(saved)
                _eventFlow.emit(AddEditTaskUiEvent.TaskSaved)
            } catch (e: InvalidTaskException) {
                _eventFlow.emit(
                    AddEditTaskUiEvent.ShowError(e.message ?: "Could not save the task.")
                )
            }
        }
    }
}
