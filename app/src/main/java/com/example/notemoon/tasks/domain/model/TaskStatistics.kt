package com.example.notemoon.tasks.domain.model

/**
 * Aggregate task metrics shown in the statistics section of the Tasks screen.
 */
data class TaskStatistics(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val completionPercentage: Int = 0
) {
    companion object {
        fun from(tasks: List<Task>): TaskStatistics {
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val percentage = if (total == 0) 0 else (completed * 100) / total
            return TaskStatistics(
                total = total,
                completed = completed,
                pending = pending,
                completionPercentage = percentage
            )
        }
    }
}
