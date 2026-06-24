package com.example.notemoon.tools.presentation.datediff

import androidx.lifecycle.ViewModel
import com.example.notemoon.tools.domain.calculator.DateDifferenceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

/** Computes the difference between a start and end date. */
@HiltViewModel
class DateDifferenceViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(DateDifferenceState(endDate = todayUtcMidnight()))
    val state: StateFlow<DateDifferenceState> = _state.asStateFlow()

    fun onStartDateSelected(millis: Long?) {
        if (millis == null) return
        _state.update { it.copy(startDate = millis) }
        recompute()
    }

    fun onEndDateSelected(millis: Long?) {
        if (millis == null) return
        _state.update { it.copy(endDate = millis) }
        recompute()
    }

    private fun recompute() {
        val s = _state.value
        val start = s.startDate
        val end = s.endDate
        if (start == null || end == null) {
            _state.update { it.copy(result = null) }
            return
        }
        _state.update { it.copy(result = DateDifferenceCalculator.calculate(start, end)) }
    }

    private fun todayUtcMidnight(): Long {
        val local = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
