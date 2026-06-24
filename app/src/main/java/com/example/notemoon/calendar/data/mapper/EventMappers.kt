package com.example.notemoon.calendar.data.mapper

import com.example.notemoon.calendar.data.local.EventEntity
import com.example.notemoon.calendar.domain.model.Event

fun EventEntity.toEvent(): Event = Event(
    id = id,
    title = title,
    description = description,
    category = category,
    date = date,
    startTime = startTime,
    endTime = endTime,
    location = location,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Event.toEntity(): EventEntity = EventEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    date = date,
    startTime = startTime,
    endTime = endTime,
    location = location,
    createdAt = createdAt,
    updatedAt = updatedAt
)
