package com.example.notemoon.tasks.domain.repository

import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the tasks data source. The domain layer depends only on this
 * interface; the Room-backed implementation lives in the data layer.
 */
interface TaskRepository {

    /** All tasks as a reactive stream (completed and pending). */
    fun getAllTasks(): Flow<List<Task>>

    /** Tasks whose title or description matches [query]. */
    fun searchTasks(query: String): Flow<List<Task>>

    /** Only completed tasks. */
    fun getCompletedTasks(): Flow<List<Task>>

    /** Only pending (not completed) tasks. */
    fun getPendingTasks(): Flow<List<Task>>

    /** Tasks with the given [priority]. */
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    /** Tasks in the given [category]. */
    fun getTasksByCategory(category: String): Flow<List<Task>>

    /** A single task by id, or null. */
    suspend fun getTaskById(id: Long): Task?

    /** Inserts or updates a task; returns its id (generated id for new tasks). */
    suspend fun upsertTask(task: Task): Long

    /** Permanently deletes a task. */
    suspend fun deleteTask(task: Task)
}
