package com.example.notemoon.tasks.domain.util

/** Direction of a sort. */
enum class SortDirection { ASCENDING, DESCENDING }

/** The field tasks are sorted by. */
enum class TaskSortType(val label: String) {
    DUE_DATE("Due date"),
    PRIORITY("Priority"),
    TITLE("Title"),
    CREATED_AT("Date created")
}

/** A sort field combined with a direction. */
data class TaskSort(
    val type: TaskSortType = TaskSortType.DUE_DATE,
    val direction: SortDirection = SortDirection.ASCENDING
)
