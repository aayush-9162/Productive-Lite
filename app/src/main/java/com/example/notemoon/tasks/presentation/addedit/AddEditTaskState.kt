package com.example.notemoon.tasks.presentation.addedit

import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.RepeatType
import com.example.notemoon.tasks.domain.model.TaskCategory

/** UI state for the Add/Edit Task screen. */
data class AddEditTaskState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: String = TaskCategory.DEFAULT,
    val isCustomCategory: Boolean = false,
    val dueDate: Long = 0L,
    val dueTime: Long = 9 * 60L, // default 09:00 once a date is picked
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val createdAt: Long? = null,
    val isLoaded: Boolean = false
) {
    val hasDueDate: Boolean get() = dueDate > 0L
}

/** One-off events from the Add/Edit Task ViewModel. */
sealed interface AddEditTaskUiEvent {
    data object TaskSaved : AddEditTaskUiEvent
    data class ShowError(val message: String) : AddEditTaskUiEvent
}
