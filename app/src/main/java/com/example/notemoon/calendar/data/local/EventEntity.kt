package com.example.notemoon.calendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table for calendar events. [date] is the UTC-midnight day; [startTime] and
 * [endTime] are minutes since midnight.
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val category: String,
    val date: Long,
    val startTime: Long,
    val endTime: Long,
    val location: String,
    val createdAt: Long,
    val updatedAt: Long
)
