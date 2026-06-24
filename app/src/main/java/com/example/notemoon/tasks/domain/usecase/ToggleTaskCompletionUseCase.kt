package com.example.notemoon.tasks.domain.usecase

import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.repository.TaskRepository

/**
 * Marks a task complete or incomplete (covers both "Mark Complete" and "Mark
 * Incomplete"). Returns the updated task so the caller can reschedule or cancel
 * its reminder accordingly.
 */
class ToggleTaskCompletionUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Task {
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            updatedAt = System.currentTimeMillis()
        )
        repository.upsertTask(updated)
        return updated
    }
}
