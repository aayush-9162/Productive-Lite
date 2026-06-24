package com.example.notemoon.calendar.domain.usecase

/** Bundles every event use case for injection into ViewModels. */
data class EventUseCases(
    val getEvents: GetEventsUseCase,
    val getEventsByDate: GetEventsByDateUseCase,
    val getEventsBetween: GetEventsBetweenUseCase,
    val getEvent: GetEventUseCase,
    val addEvent: AddEventUseCase,
    val deleteEvent: DeleteEventUseCase
)
