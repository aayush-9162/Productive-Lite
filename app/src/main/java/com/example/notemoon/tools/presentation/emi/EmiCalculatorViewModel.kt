package com.example.notemoon.tools.presentation.emi

import androidx.lifecycle.ViewModel
import com.example.notemoon.tools.domain.calculator.EmiCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Recomputes the EMI live as the loan amount, rate or tenure change. */
@HiltViewModel
class EmiCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(EmiCalculatorState())
    val state: StateFlow<EmiCalculatorState> = _state.asStateFlow()

    fun onAmountChange(value: String) {
        _state.update { it.copy(amount = value.filterNumeric()) }
        recompute()
    }

    fun onRateChange(value: String) {
        _state.update { it.copy(rate = value.filterNumeric()) }
        recompute()
    }

    fun onTenureChange(value: String) {
        _state.update { it.copy(tenure = value.filterNumeric()) }
        recompute()
    }

    fun onTenureUnitChange(inYears: Boolean) {
        _state.update { it.copy(tenureInYears = inYears) }
        recompute()
    }

    private fun recompute() {
        val s = _state.value
        val principal = s.amount.toDoubleOrNull()
        val rate = s.rate.toDoubleOrNull()
        val tenure = s.tenure.toDoubleOrNull()

        if (principal == null || rate == null || tenure == null) {
            _state.update { it.copy(result = null, error = null) }
            return
        }

        val months = if (s.tenureInYears) (tenure * 12).toInt() else tenure.toInt()
        try {
            val result = EmiCalculator.calculate(principal, rate, months)
            _state.update { it.copy(result = result, error = null) }
        } catch (e: IllegalArgumentException) {
            _state.update { it.copy(result = null, error = e.message) }
        }
    }

    private fun String.filterNumeric(): String =
        filterIndexed { index, c -> c.isDigit() || (c == '.' && !substring(0, index).contains('.')) }
}
