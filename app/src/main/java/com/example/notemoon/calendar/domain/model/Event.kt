package com.example.notemoon.calendar.domain.model

/**
 * Domain representation of a calendar event. Kept separate from the Room
 * [com.example.notemoon.calendar.data.local.EventEntity].
 *
 * Date/time encoding mirrors the Tasks module so events and task due-dates line
 * up on the same grid:
 *  - [date]: the day as UTC-midnight epoch millis (what the Material date picker
 *    reports).
 *  - [startTime] / [endTime]: minutes since midnight (hour * 60 + minute).
 */
data class Event(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val category: String = EventCategory.DEFAULT,
    val date: Long,
    val startTime: Long = 9 * 60L,
    val endTime: Long = 10 * 60L,
    val location: String = "",
    val createdAt: Long,
    val updatedAt: Long
)

/** Thrown when an event fails validation (e.g. a blank title). */
class InvalidEventException(message: String) : Exception(message)
