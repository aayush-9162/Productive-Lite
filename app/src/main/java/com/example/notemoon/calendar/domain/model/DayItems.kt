package com.example.notemoon.calendar.domain.model

import com.example.notemoon.tasks.domain.model.Task

/**
 * The events and tasks falling on a single calendar day, used both for the
 * day-cell indicators (counts) and for the day/agenda lists.
 */
data class DayItems(
    val date: Long,
    val events: List<Event> = emptyList(),
    val tasks: List<Task> = emptyList()
) {
    val eventCount: Int get() = events.size
    val taskCount: Int get() = tasks.size
    val pendingTaskCount: Int get() = tasks.count { !it.isCompleted }
    val completedTaskCount: Int get() = tasks.count { it.isCompleted }
    val isEmpty: Boolean get() = events.isEmpty() && tasks.isEmpty()

    /** Events and tasks merged into one list, ordered by time. */
    fun toSortedItems(): List<CalendarItem> {
        val merged = events.map { CalendarItem.EventEntry(it) } +
            tasks.map { CalendarItem.TaskEntry(it) }
        return merged.sortedBy { it.sortTime }
    }
}
