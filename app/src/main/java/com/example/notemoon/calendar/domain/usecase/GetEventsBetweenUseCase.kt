package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import kotlinx.coroutines.flow.Flow

/** Streams events whose day falls within [start]..[end] inclusive. */
class GetEventsBetweenUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(start: Long, end: Long): Flow<List<Event>> =
        repository.getEventsBetween(
            CalendarDateUtils.startOfDay(start),
            CalendarDateUtils.startOfDay(end)
        )
}
