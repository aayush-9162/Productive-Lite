package com.example.notemoon.tools.presentation.age

import androidx.lifecycle.ViewModel
import com.example.notemoon.tools.domain.calculator.AgeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

/** Computes age whenever a birth date is picked. */
@HiltViewModel
class AgeCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AgeCalculatorState())
    val state: StateFlow<AgeCalculatorState> = _state.asStateFlow()

    fun onBirthDateSelected(millis: Long?) {
        if (millis == null) return
        val today = todayUtcMidnight()
        try {
            val result = AgeCalculator.calculate(millis, today)
            _state.update { it.copy(birthDate = millis, result = result, error = null) }
        } catch (e: IllegalArgumentException) {
            _state.update { it.copy(birthDate = millis, result = null, error = e.message) }
        }
    }

    private fun todayUtcMidnight(): Long {
        val local = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
