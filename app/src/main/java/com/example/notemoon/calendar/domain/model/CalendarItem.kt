package com.example.notemoon.calendar.domain.model

import com.example.notemoon.tasks.domain.model.Task

/**
 * A single entry shown in day/agenda lists — either a calendar [Event] or a
 * [Task] due that day (the Task-module integration). [sortTime] is minutes since
 * midnight, used to order a day's entries chronologically.
 */
sealed interface CalendarItem {
    val sortTime: Long

    data class EventEntry(val event: Event) : CalendarItem {
        override val sortTime: Long get() = event.startTime
    }

    data class TaskEntry(val task: Task) : CalendarItem {
        override val sortTime: Long get() = task.dueTime
    }
}
