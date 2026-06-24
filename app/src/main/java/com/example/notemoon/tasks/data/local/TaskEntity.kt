package com.example.notemoon.tasks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table for tasks. Enum-like fields (priority, repeatType) are stored as
 * their string names; category is a free string.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val priority: String,
    val category: String,
    val dueDate: Long,
    val dueTime: Long,
    val isCompleted: Boolean,
    val reminderEnabled: Boolean,
    val repeatType: String,
    val createdAt: Long,
    val updatedAt: Long
)
