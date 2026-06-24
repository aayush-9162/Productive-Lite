package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository

/** Loads a single event by id (Add/Edit and Details screens). */
class GetEventUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(id: Long): Event? = repository.getEventById(id)
}
