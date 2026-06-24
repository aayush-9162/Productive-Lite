package com.example.notemoon.tasks.domain.model

/**
 * Domain representation of a task. Kept separate from the Room
 * [com.example.notemoon.tasks.data.local.TaskEntity] so the UI and business
 * logic never depend on the database schema.
 *
 * [dueDate] is the epoch-millis of the chosen day (UTC midnight from the date
 * picker) or 0 when no due date is set. [dueTime] is the minutes-since-midnight
 * of the chosen time. Combine them with
 * [com.example.notemoon.tasks.domain.util.combineDateAndTime] to get the trigger.
 */
data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: String = TaskCategory.DEFAULT,
    val dueDate: Long = 0L,
    val dueTime: Long = 0L,
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val createdAt: Long,
    val updatedAt: Long
) {
    /** True when the task has a usable due date set. */
    val hasDueDate: Boolean get() = dueDate > 0L
}

/** Thrown when a task fails validation (e.g. a blank title). */
class InvalidTaskException(message: String) : Exception(message)
