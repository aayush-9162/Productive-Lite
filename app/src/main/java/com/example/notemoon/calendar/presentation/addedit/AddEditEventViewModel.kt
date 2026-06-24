package com.example.notemoon.calendar.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.calendar.domain.model.Event
import com.example.notemoon.calendar.domain.model.EventCategory
import com.example.notemoon.calendar.domain.model.InvalidEventException
import com.example.notemoon.calendar.domain.usecase.EventUseCases
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.navigation.EVENT_DATE_ARG
import com.example.notemoon.calendar.presentation.navigation.EVENT_ID_ARG
import com.example.notemoon.calendar.presentation.navigation.NO_DATE
import com.example.notemoon.calendar.presentation.navigation.NO_EVENT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the Add/Edit Event screen: loads an event when editing (or pre-fills the
 * date when adding from a calendar day) and persists changes on save.
 */
@HiltViewModel
class AddEditEventViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditEventState())
    val state: StateFlow<AddEditEventState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddEditEventUiEvent>()
    val eventFlow: SharedFlow<AddEditEventUiEvent> = _eventFlow.asSharedFlow()

    init {
        val eventId = savedStateHandle.get<Long>(EVENT_ID_ARG) ?: NO_EVENT_ID
        val presetDate = savedStateHandle.get<Long>(EVENT_DATE_ARG) ?: NO_DATE

        if (eventId != NO_EVENT_ID) {
            viewModelScope.launch {
                eventUseCases.getEvent(eventId)?.let { event ->
                    _state.update {
                        it.copy(
                            eventId = event.id,
                            title = event.title,
                            description = event.description,
                            category = event.category,
                            isCustomCategory = event.category !in EventCategory.presets,
                            date = event.date,
                            startTime = event.startTime,
                            endTime = event.endTime,
                            location = event.location,
                            createdAt = event.createdAt,
                            isLoaded = true
                        )
                    }
                } ?: _state.update { it.copy(isLoaded = true) }
            }
        } else {
            val date = if (presetDate != NO_DATE) presetDate else CalendarDateUtils.today()
            _state.update { it.copy(date = date, isLoaded = true) }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value) }
    fun onLocationChange(value: String) = _state.update { it.copy(location = value) }

    fun onPresetCategorySelected(category: String) =
        _state.update { it.copy(category = category, isCustomCategory = false) }

    fun onCustomCategorySelected() =
        _state.update { it.copy(isCustomCategory = true, category = "") }

    fun onCustomCategoryChange(value: String) = _state.update { it.copy(category = value) }

    fun onDateChange(dateUtcMillis: Long?) =
        _state.update { it.copy(date = dateUtcMillis ?: it.date) }

    fun onStartTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(startTime = CalendarDateUtils.packTime(hour, minute)) }

    fun onEndTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(endTime = CalendarDateUtils.packTime(hour, minute)) }

    fun saveEvent() {
        val current = _state.value
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val event = Event(
                id = current.eventId ?: 0L,
                title = current.title.trim(),
                description = current.description.trim(),
                category = current.category.trim().ifBlank { EventCategory.DEFAULT },
                date = if (current.date > 0L) current.date else CalendarDateUtils.today(),
                startTime = current.startTime,
                endTime = current.endTime,
                location = current.location.trim(),
                createdAt = current.createdAt ?: now,
                updatedAt = now
            )
            try {
                eventUseCases.addEvent(event)
                _eventFlow.emit(AddEditEventUiEvent.EventSaved)
            } catch (e: InvalidEventException) {
                _eventFlow.emit(AddEditEventUiEvent.ShowError(e.message ?: "Could not save the event."))
            }
        }
    }
}
