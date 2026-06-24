package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.model.InvalidEventException
import com.example.notemoon.calendar.domain.repository.EventRepository

/**
 * Creates or updates an event. Validates that it has a title and that the end
 * time is not before the start time, then persists it and returns its id.
 */
class AddEventUseCase(
    private val repository: EventRepository
) {
    @Throws(InvalidEventException::class)
    suspend operator fun invoke(event: Event): Long {
        if (event.title.isBlank()) {
            throw InvalidEventException("An event needs a title.")
        }
        if (event.endTime < event.startTime) {
            throw InvalidEventException("The end time can't be before the start time.")
        }
        return repository.upsertEvent(event)
    }
}
