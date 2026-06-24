package com.example.notemoon.calendar.domain.repository

import com.example.notemoon.calendar.domain.model.Event
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the events data source. The domain depends only on this; the
 * Room-backed implementation lives in the data layer.
 */
interface EventRepository {

    /** All events as a reactive stream. */
    fun getAllEvents(): Flow<List<Event>>

    /** Events whose title, description or location matches [query]. */
    fun searchEvents(query: String): Flow<List<Event>>

    /** Events on a single day (UTC-midnight [date]). */
    fun getEventsByDate(date: Long): Flow<List<Event>>

    /** Events whose day falls within [start]..[end] inclusive. */
    fun getEventsBetween(start: Long, end: Long): Flow<List<Event>>

    /** A single event by id, or null. */
    suspend fun getEventById(id: Long): Event?

    /** Inserts or updates an event; returns its id. */
    suspend fun upsertEvent(event: Event): Long

    /** Permanently deletes an event. */
    suspend fun deleteEvent(event: Event)
}
