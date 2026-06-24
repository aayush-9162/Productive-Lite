package com.example.notemoon.tasks.data.mapper

import com.example.notemoon.tasks.data.local.TaskEntity
import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.RepeatType
import com.example.notemoon.tasks.domain.model.Task

/** Maps a Room [TaskEntity] to a domain [Task], decoding enum strings safely. */
fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    priority = Priority.from(priority),
    category = category,
    dueDate = dueDate,
    dueTime = dueTime,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled,
    repeatType = RepeatType.from(repeatType),
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Maps a domain [Task] to a Room [TaskEntity], encoding enums as their names. */
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    priority = priority.name,
    category = category,
    dueDate = dueDate,
    dueTime = dueTime,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled,
    repeatType = repeatType.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)
