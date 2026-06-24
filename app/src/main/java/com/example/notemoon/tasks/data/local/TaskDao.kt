package com.example.notemoon.tasks.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Room data-access object for [TaskEntity]. */
@Dao
interface TaskDao {

    /** Insert a task, returning the generated row id. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    /** Update an existing task. */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /** Insert a new task or update an existing one; returns its row id. */
    @Upsert
    suspend fun upsertTask(task: TaskEntity): Long

    /** Delete a task. */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /** All tasks, newest-created first. */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /** A single task by id. */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    /** All tasks as a one-shot list, used by backup/export. */
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksList(): List<TaskEntity>

    /** Search tasks by title or description. */
    @Query(
        "SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    /** Completed tasks. */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    /** Pending (not completed) tasks. */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    /** Tasks with a given priority. */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

    /** Tasks in a given category. */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>
}
