package com.example.notemoon.calendar.presentation.addedit

import com.example.notemoon.calendar.domain.model.EventCategory

/** UI state for the Add/Edit Event screen. */
data class AddEditEventState(
    val eventId: Long? = null,
    val title: String = "",
    val description: String = "",
    val category: String = EventCategory.DEFAULT,
    val isCustomCategory: Boolean = false,
    val date: Long = 0L,
    val startTime: Long = 9 * 60L,
    val endTime: Long = 10 * 60L,
    val location: String = "",
    val createdAt: Long? = null,
    val isLoaded: Boolean = false
)

/** One-off events from the Add/Edit Event ViewModel. */
sealed interface AddEditEventUiEvent {
    data object EventSaved : AddEditEventUiEvent
    data class ShowError(val message: String) : AddEditEventUiEvent
}
