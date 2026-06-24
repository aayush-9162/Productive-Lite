package com.example.notemoon.tasks.domain.usecase

import com.example.notemoon.tasks.domain.model.TaskStatistics
import com.example.notemoon.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Streams aggregate task statistics (total, completed, pending, completion %)
 * derived from all tasks, for the statistics section of the Tasks screen.
 */
class GetTaskStatisticsUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<TaskStatistics> {
        return repository.getAllTasks().map { TaskStatistics.from(it) }
    }
}
