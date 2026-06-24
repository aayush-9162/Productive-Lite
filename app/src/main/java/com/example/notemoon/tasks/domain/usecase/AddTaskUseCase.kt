package com.example.notemoon.tasks.domain.usecase

import com.example.notemoon.tasks.domain.model.InvalidTaskException
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.repository.TaskRepository

/**
 * Creates or updates a task. Validates that the title is not blank, then
 * persists it and returns its id (the generated id for a new task).
 */
class AddTaskUseCase(
    private val repository: TaskRepository
) {
    @Throws(InvalidTaskException::class)
    suspend operator fun invoke(task: Task): Long {
        if (task.title.isBlank()) {
            throw InvalidTaskException("A task needs a title.")
        }
        return repository.upsertTask(task)
    }
}
