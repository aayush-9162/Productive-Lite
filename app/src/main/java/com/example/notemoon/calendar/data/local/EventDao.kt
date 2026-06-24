package com.example.notemoon.calendar.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Room data-access object for [EventEntity]. */
@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Upsert
    suspend fun upsertEvent(event: EventEntity): Long

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    /** All events, ordered by day then start time. */
    @Query("SELECT * FROM events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?

    /** All events as a one-shot list, used by backup/export. */
    @Query("SELECT * FROM events")
    suspend fun getAllEventsList(): List<EventEntity>

    @Query(
        "SELECT * FROM events WHERE title LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' " +
            "OR location LIKE '%' || :query || '%' ORDER BY date ASC, startTime ASC"
    )
    fun searchEvents(query: String): Flow<List<EventEntity>>

    /** Events on a single day. */
    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: Long): Flow<List<EventEntity>>

    /** Events whose day falls within [start]..[end] inclusive. */
    @Query("SELECT * FROM events WHERE date BETWEEN :start AND :end ORDER BY date ASC, startTime ASC")
    fun getEventsBetween(start: Long, end: Long): Flow<List<EventEntity>>
}
