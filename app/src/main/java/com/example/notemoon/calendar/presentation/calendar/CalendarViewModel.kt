package com.example.notemoon.calendar.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.calendar.domain.model.DayItems
import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.usecase.EventUseCases
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Calendar screen. Merges calendar [Event]s with Task-module due
 * dates into a per-day map ([DayItems]) used by every view, and manages the
 * selected date, displayed month, current view tab and search.
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val today = CalendarDateUtils.today()

    private val _state = MutableStateFlow(
        CalendarUiState(
            today = today,
            displayedMonth = CalendarDateUtils.firstOfMonth(today),
            selectedDate = today
        )
    )
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    private var recentlyDeletedEvent: Event? = null

    init {
        combine(
            eventUseCases.getEvents(),
            taskUseCases.getTasks()
        ) { events, tasks -> events to tasks }
            .onEach { (events, tasks) ->
                _state.update {
                    it.copy(
                        allEvents = events,
                        itemsByDay = buildItemsByDay(events, tasks),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onViewChange(view: CalendarView) = _state.update { it.copy(view = view) }

    fun onDateSelected(date: Long) =
        _state.update { it.copy(selectedDate = CalendarDateUtils.startOfDay(date)) }

    fun goToToday() = _state.update {
        it.copy(
            selectedDate = today,
            displayedMonth = CalendarDateUtils.firstOfMonth(today)
        )
    }

    /** Header back-arrow: month back, week back 7 days, or day back 1 day. */
    fun onPrevious() = shift(-1)

    /** Header forward-arrow. */
    fun onNext() = shift(1)

    private fun shift(direction: Int) {
        _state.update { s ->
            when (s.view) {
                CalendarView.MONTH ->
                    s.copy(displayedMonth = CalendarDateUtils.addMonths(s.displayedMonth, direction))

                CalendarView.WEEK -> {
                    val newDate = CalendarDateUtils.addDays(s.selectedDate, 7 * direction)
                    s.copy(selectedDate = newDate, displayedMonth = CalendarDateUtils.firstOfMonth(newDate))
                }

                CalendarView.DAY -> {
                    val newDate = CalendarDateUtils.addDays(s.selectedDate, direction)
                    s.copy(selectedDate = newDate, displayedMonth = CalendarDateUtils.firstOfMonth(newDate))
                }

                CalendarView.AGENDA -> s
            }
        }
    }

    fun onSearchQueryChange(query: String) = _state.update { it.copy(searchQuery = query) }

    fun setSearchActive(active: Boolean) = _state.update {
        it.copy(isSearchActive = active, searchQuery = if (active) it.searchQuery else "")
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventUseCases.deleteEvent(event)
            recentlyDeletedEvent = event
        }
    }

    fun restoreEvent() {
        viewModelScope.launch {
            recentlyDeletedEvent?.let { eventUseCases.addEvent(it) }
            recentlyDeletedEvent = null
        }
    }

    private fun buildItemsByDay(events: List<Event>, tasks: List<Task>): Map<Long, DayItems> {
        val eventsByDay = events.groupBy { CalendarDateUtils.startOfDay(it.date) }
        val tasksByDay = tasks
            .filter { it.hasDueDate }
            .groupBy { CalendarDateUtils.startOfDay(it.dueDate) }

        val days = eventsByDay.keys + tasksByDay.keys
        return days.associateWith { day ->
            DayItems(
                date = day,
                events = eventsByDay[day].orEmpty(),
                tasks = tasksByDay[day].orEmpty()
            )
        }
    }
}
