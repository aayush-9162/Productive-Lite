package com.example.notemoon.home.presentation

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.tasks.domain.model.Task

/** UI state for the Home dashboard. */
data class HomeState(
    val today: Long = 0L,
    val todaysEvents: List<Event> = emptyList(),
    val upcomingEvents: List<Event> = emptyList(),
    val todaysTasks: List<Task> = emptyList(),
    val totalEventsToday: Int = 0,
    val pendingTasksToday: Int = 0,
    val isLoading: Boolean = true
)
