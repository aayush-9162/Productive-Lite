package com.example.notemoon.alarm.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemoon.alarm.domain.model.Alarm
import com.example.notemoon.alarm.domain.repository.AlarmRepository
import com.example.notemoon.alarm.domain.scheduler.AlarmScheduler
import com.example.notemoon.alarm.domain.util.AlarmSchedule
import com.example.notemoon.alarm.domain.util.RepeatPreset
import com.example.notemoon.alarm.presentation.navigation.AlarmDestinations
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

/** Backs the add/edit alarm screen. Loads an existing alarm when editing and on
 *  save persists it and (re)schedules it via [AlarmScheduler]. */
@HiltViewModel
class AddEditAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditAlarmState())
    val state: StateFlow<AddEditAlarmState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AddEditAlarmEvent>()
    val events: SharedFlow<AddEditAlarmEvent> = _events.asSharedFlow()

    init {
        val id = savedStateHandle.get<Long>(AlarmDestinations.ALARM_ID_ARG)
            ?: AlarmDestinations.NO_ALARM_ID
        if (id != AlarmDestinations.NO_ALARM_ID) {
            viewModelScope.launch {
                repository.getAlarmById(id)?.let { a ->
                    _state.update {
                        it.copy(
                            alarmId = a.id,
                            hour = a.hour,
                            minute = a.minute,
                            label = a.label,
                            enabled = a.enabled,
                            repeatDays = a.repeatDays,
                            vibrate = a.vibrate,
                            soundUri = a.soundUri,
                            snoozeMinutes = a.snoozeMinutes,
                            mathToDismiss = a.mathToDismiss,
                            mathQuestions = a.mathQuestions,
                            createdAt = a.createdAt,
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun onTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(hour = hour, minute = minute) }

    fun onLabelChange(value: String) = _state.update { it.copy(label = value) }

    fun onPresetSelected(preset: RepeatPreset) = _state.update {
        it.copy(repeatDays = AlarmSchedule.daysForPreset(preset, it.repeatDays))
    }

    fun onToggleDay(day: Int) = _state.update {
        val days = it.repeatDays.toMutableSet()
        if (!days.add(day)) days.remove(day)
        it.copy(repeatDays = days)
    }

    fun onVibrateChange(value: Boolean) = _state.update { it.copy(vibrate = value) }

    fun onSoundChange(uri: String?) = _state.update { it.copy(soundUri = uri) }

    fun onSnoozeChange(minutes: Int) = _state.update { it.copy(snoozeMinutes = minutes) }

    fun onMathToDismissChange(value: Boolean) = _state.update { it.copy(mathToDismiss = value) }

    fun onMathQuestionsChange(count: Int) = _state.update { it.copy(mathQuestions = count) }

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            val alarm = Alarm(
                id = s.alarmId ?: 0L,
                hour = s.hour,
                minute = s.minute,
                label = s.label.trim(),
                enabled = true,
                repeatDays = s.repeatDays,
                vibrate = s.vibrate,
                soundUri = s.soundUri,
                snoozeMinutes = s.snoozeMinutes,
                mathToDismiss = s.mathToDismiss,
                mathQuestions = s.mathQuestions,
                createdAt = s.createdAt ?: System.currentTimeMillis()
            )
            val savedId = repository.upsertAlarm(alarm)
            scheduler.schedule(alarm.copy(id = if (alarm.id == 0L) savedId else alarm.id))
            _events.emit(AddEditAlarmEvent.Saved)
        }
    }

    fun delete() {
        val id = _state.value.alarmId ?: return
        viewModelScope.launch {
            scheduler.cancel(id)
            repository.getAlarmById(id)?.let { repository.deleteAlarm(it) }
            _events.emit(AddEditAlarmEvent.Saved)
        }
    }
}

data class AddEditAlarmState(
    val alarmId: Long? = null,
    val hour: Int = 8,
    val minute: Int = 0,
    val label: String = "",
    val enabled: Boolean = true,
    val repeatDays: Set<Int> = emptySet(),
    val vibrate: Boolean = true,
    val soundUri: String? = null,
    val snoozeMinutes: Int = 10,
    val mathToDismiss: Boolean = false,
    val mathQuestions: Int = 2,
    val createdAt: Long? = null,
    val isEditing: Boolean = false
)

sealed interface AddEditAlarmEvent {
    data object Saved : AddEditAlarmEvent
}
