package com.example.notemoon.tasks.domain.usecase

import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.repository.TaskRepository
import com.example.notemoon.tasks.domain.util.SortDirection
import com.example.notemoon.tasks.domain.util.StatusFilter
import com.example.notemoon.tasks.domain.util.TaskFilter
import com.example.notemoon.tasks.domain.util.TaskSort
import com.example.notemoon.tasks.domain.util.TaskSortType
import com.example.notemoon.tasks.domain.util.combineDateAndTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Streams the task list for the Tasks screen, applying search, filtering and
 * sorting in memory over a single reactive source so the list updates whenever
 * the data, query, filter or sort changes.
 */
class GetTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(
        query: String = "",
        filter: TaskFilter = TaskFilter(),
        sort: TaskSort = TaskSort()
    ): Flow<List<Task>> {
        return repository.getAllTasks().map { tasks ->
            tasks
                .filterByQuery(query)
                .filterBy(filter)
                .sortedBy(sort)
        }
    }

    private fun List<Task>.filterByQuery(query: String): List<Task> {
        if (query.isBlank()) return this
        return filter { task ->
            task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)
        }
    }

    private fun List<Task>.filterBy(filter: TaskFilter): List<Task> {
        return filter { task ->
            val statusOk = when (filter.status) {
                StatusFilter.ALL -> true
                StatusFilter.PENDING -> !task.isCompleted
                StatusFilter.COMPLETED -> task.isCompleted
            }
            val priorityOk = filter.priority?.let { task.priority == it } ?: true
            val categoryOk = filter.category?.let { task.category.equals(it, ignoreCase = true) } ?: true
            statusOk && priorityOk && categoryOk
        }
    }

    private fun List<Task>.sortedBy(sort: TaskSort): List<Task> {
        val ascending = sort.direction == SortDirection.ASCENDING
        return when (sort.type) {
            TaskSortType.DUE_DATE -> {
                val (withDate, withoutDate) = partition { it.hasDueDate }
                val cmp = compareBy<Task> { combineDateAndTime(it.dueDate, it.dueTime) }
                val ordered = withDate.sortedWith(if (ascending) cmp else cmp.reversed())
                ordered + withoutDate // tasks without a due date always go last
            }

            TaskSortType.PRIORITY -> {
                val cmp = compareBy<Task> { it.priority.ordinal }
                sortedWith(if (ascending) cmp else cmp.reversed())
            }

            TaskSortType.TITLE -> {
                val cmp = compareBy<Task> { it.title.lowercase() }
                sortedWith(if (ascending) cmp else cmp.reversed())
            }

            TaskSortType.CREATED_AT -> {
                val cmp = compareBy<Task> { it.createdAt }
                sortedWith(if (ascending) cmp else cmp.reversed())
            }
        }
    }
}
