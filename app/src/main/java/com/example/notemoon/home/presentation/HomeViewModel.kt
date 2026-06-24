package com.example.notemoon.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.calendar.domain.usecase.EventUseCases
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Aggregates calendar events and task due-dates for the Home dashboard:
 * today's events, upcoming events (next 30 days) and today's tasks.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    eventUseCases: EventUseCases,
    taskUseCases: TaskUseCases
) : ViewModel() {

    private val today = CalendarDateUtils.today()
    private val horizon = CalendarDateUtils.addDays(today, 30)

    private val _state = MutableStateFlow(HomeState(today = today))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        combine(
            eventUseCases.getEvents(),
            taskUseCases.getTasks()
        ) { events, tasks -> events to tasks }
            .onEach { (events, tasks) ->
                val todaysEvents = events.filter { CalendarDateUtils.isSameDay(it.date, today) }
                val upcomingEvents = events.filter {
                    val d = CalendarDateUtils.startOfDay(it.date)
                    d > today && d <= horizon
                }.sortedWith(compareBy({ it.date }, { it.startTime }))
                val todaysTasks = tasks.filter {
                    it.hasDueDate && CalendarDateUtils.isSameDay(it.dueDate, today)
                }.sortedBy { it.dueTime }

                _state.update {
                    it.copy(
                        todaysEvents = todaysEvents,
                        upcomingEvents = upcomingEvents,
                        todaysTasks = todaysTasks,
                        totalEventsToday = todaysEvents.size,
                        pendingTasksToday = todaysTasks.count { t -> !t.isCompleted },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
