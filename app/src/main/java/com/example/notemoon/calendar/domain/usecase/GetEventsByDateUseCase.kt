package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import kotlinx.coroutines.flow.Flow

/** Streams the events on a single day. */
class GetEventsByDateUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(date: Long): Flow<List<Event>> =
        repository.getEventsByDate(CalendarDateUtils.startOfDay(date))
}
