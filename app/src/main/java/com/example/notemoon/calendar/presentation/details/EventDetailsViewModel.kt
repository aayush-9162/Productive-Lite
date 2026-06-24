package com.example.notemoon.calendar.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.usecase.EventUseCases
import com.example.notemoon.calendar.presentation.navigation.EVENT_ID_ARG
import com.example.notemoon.calendar.presentation.navigation.NO_EVENT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailsState(
    val event: Event? = null,
    val isLoading: Boolean = true
)

/** Backs the Event Details screen: loads the event and supports deletion. */
@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailsState())
    val state: StateFlow<EventDetailsState> = _state.asStateFlow()

    private val _navigateBack = Channel<Unit>()
    val navigateBack = _navigateBack.receiveAsFlow()

    private val eventId: Long = savedStateHandle.get<Long>(EVENT_ID_ARG) ?: NO_EVENT_ID

    init {
        viewModelScope.launch {
            val event = if (eventId == NO_EVENT_ID) null else eventUseCases.getEvent(eventId)
            _state.update { it.copy(event = event, isLoading = false) }
        }
    }

    fun deleteEvent() {
        val event = _state.value.event ?: return
        viewModelScope.launch {
            eventUseCases.deleteEvent(event)
            _navigateBack.send(Unit)
        }
    }
}
