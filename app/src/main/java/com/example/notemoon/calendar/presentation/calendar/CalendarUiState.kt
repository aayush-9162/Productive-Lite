package com.example.notemoon.calendar.presentation.calendar

import com.example.notemoon.calendar.domain.model.DayItems
import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.util.CalendarDateUtils

/** The four calendar view modes (the screen's tabs). */
enum class CalendarView(val label: String) {
    MONTH("Month"),
    WEEK("Week"),
    DAY("Day"),
    AGENDA("Agenda")
}

/** UI state for the Calendar screen. */
data class CalendarUiState(
    val view: CalendarView = CalendarView.MONTH,
    val today: Long = 0L,
    val displayedMonth: Long = 0L,
    val selectedDate: Long = 0L,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val allEvents: List<Event> = emptyList(),
    val itemsByDay: Map<Long, DayItems> = emptyMap(),
    val isLoading: Boolean = true
) {
    /** Events and tasks for a given day (never null). */
    fun itemsFor(day: Long): DayItems =
        itemsByDay[CalendarDateUtils.startOfDay(day)] ?: DayItems(CalendarDateUtils.startOfDay(day))

    /** Events matching the current search query. */
    fun searchResults(): List<Event> {
        if (searchQuery.isBlank()) return emptyList()
        return allEvents.filter { e ->
            e.title.contains(searchQuery, ignoreCase = true) ||
                e.description.contains(searchQuery, ignoreCase = true) ||
                e.location.contains(searchQuery, ignoreCase = true)
        }
    }
}
