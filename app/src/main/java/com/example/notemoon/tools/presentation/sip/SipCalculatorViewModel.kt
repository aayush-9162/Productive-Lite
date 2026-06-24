package com.example.notemoon.tools.presentation.sip

import androidx.lifecycle.ViewModel
import com.example.notemoon.tools.domain.calculator.SipCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Recomputes the SIP projection live as inputs change. */
@HiltViewModel
class SipCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SipCalculatorState())
    val state: StateFlow<SipCalculatorState> = _state.asStateFlow()

    fun onAmountChange(value: String) {
        _state.update { it.copy(monthlyAmount = value.filterNumeric()) }
        recompute()
    }

    fun onReturnChange(value: String) {
        _state.update { it.copy(annualReturn = value.filterNumeric()) }
        recompute()
    }

    fun onDurationChange(value: String) {
        _state.update { it.copy(duration = value.filterNumeric()) }
        recompute()
    }

    fun onDurationUnitChange(inYears: Boolean) {
        _state.update { it.copy(durationInYears = inYears) }
        recompute()
    }

    private fun recompute() {
        val s = _state.value
        val amount = s.monthlyAmount.toDoubleOrNull()
        val rate = s.annualReturn.toDoubleOrNull()
        val duration = s.duration.toDoubleOrNull()

        if (amount == null || rate == null || duration == null) {
            _state.update { it.copy(result = null, error = null) }
            return
        }

        val months = if (s.durationInYears) (duration * 12).toInt() else duration.toInt()
        try {
            val result = SipCalculator.calculate(amount, rate, months)
            _state.update { it.copy(result = result, error = null) }
        } catch (e: IllegalArgumentException) {
            _state.update { it.copy(result = null, error = e.message) }
        }
    }

    private fun String.filterNumeric(): String =
        filterIndexed { index, c -> c.isDigit() || (c == '.' && !substring(0, index).contains('.')) }
}
