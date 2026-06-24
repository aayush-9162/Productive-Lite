package com.example.notemoon.calendar.domain.usecase

import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Streams all events. When a [query] is supplied the list is filtered (in
 * memory, over the same reactive source) by title, description or location.
 */
class GetEventsUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(query: String = ""): Flow<List<Event>> {
        return repository.getAllEvents().map { events ->
            if (query.isBlank()) {
                events
            } else {
                events.filter { event ->
                    event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true) ||
                        event.location.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
