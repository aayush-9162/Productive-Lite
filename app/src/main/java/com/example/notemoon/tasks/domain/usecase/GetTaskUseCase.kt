package com.example.notemoon.tasks.domain.usecase

import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.repository.TaskRepository

/** Loads a single task by id, used by the Add/Edit and Details screens. */
class GetTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long): Task? = repository.getTaskById(id)
}
