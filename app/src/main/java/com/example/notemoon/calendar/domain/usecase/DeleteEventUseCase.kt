package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository

/** Permanently deletes an event. */
class DeleteEventUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) = repository.deleteEvent(event)
}
