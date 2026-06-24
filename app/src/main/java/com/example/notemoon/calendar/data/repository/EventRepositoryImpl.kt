package com.example.notemoon.calendar.data.repository

import com.example.notemoon.calendar.data.local.EventDao
import com.example.notemoon.calendar.data.local.EventEntity
import com.example.notemoon.calendar.data.mapper.toEntity
import com.example.notemoon.calendar.data.mapper.toEvent
import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed implementation of [EventRepository]. */
class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> = dao.getAllEvents().mapEntities()

    override fun searchEvents(query: String): Flow<List<Event>> =
        dao.searchEvents(query).mapEntities()

    override fun getEventsByDate(date: Long): Flow<List<Event>> =
        dao.getEventsByDate(date).mapEntities()

    override fun getEventsBetween(start: Long, end: Long): Flow<List<Event>> =
        dao.getEventsBetween(start, end).mapEntities()

    override suspend fun getEventById(id: Long): Event? = dao.getEventById(id)?.toEvent()

    override suspend fun upsertEvent(event: Event): Long = dao.upsertEvent(event.toEntity())

    override suspend fun deleteEvent(event: Event) = dao.deleteEvent(event.toEntity())

    private fun Flow<List<EventEntity>>.mapEntities(): Flow<List<Event>> =
        map { list -> list.map { it.toEvent() } }
}
