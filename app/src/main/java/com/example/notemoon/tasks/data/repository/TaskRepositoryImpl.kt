package com.example.notemoon.tasks.data.repository

import com.example.notemoon.tasks.data.local.TaskDao
import com.example.notemoon.tasks.data.mapper.toEntity
import com.example.notemoon.tasks.data.mapper.toTask
import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed implementation of [TaskRepository]. */
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        dao.getAllTasks().mapEntities()

    override fun searchTasks(query: String): Flow<List<Task>> =
        dao.searchTasks(query).mapEntities()

    override fun getCompletedTasks(): Flow<List<Task>> =
        dao.getCompletedTasks().mapEntities()

    override fun getPendingTasks(): Flow<List<Task>> =
        dao.getPendingTasks().mapEntities()

    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
        dao.getTasksByPriority(priority.name).mapEntities()

    override fun getTasksByCategory(category: String): Flow<List<Task>> =
        dao.getTasksByCategory(category).mapEntities()

    override suspend fun getTaskById(id: Long): Task? =
        dao.getTaskById(id)?.toTask()

    override suspend fun upsertTask(task: Task): Long =
        dao.upsertTask(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        dao.deleteTask(task.toEntity())

    private fun Flow<List<com.example.notemoon.tasks.data.local.TaskEntity>>.mapEntities(): Flow<List<Task>> =
        map { list -> list.map { it.toTask() } }
}
