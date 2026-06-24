package com.example.notemoon.tools.presentation.standard

import androidx.lifecycle.ViewModel
import com.example.notemoon.tools.domain.calculator.StandardCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Holds and edits the standard calculator's expression, recomputing the live
 *  preview after every keystroke. */
@HiltViewModel
class StandardCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(StandardCalculatorState())
    val state: StateFlow<StandardCalculatorState> = _state.asStateFlow()

    private val operators = setOf('+', '−', '×', '÷')

    private fun current() = _state.value.expression

    private fun setExpression(expr: String) {
        val preview = try {
            if (expr.isBlank()) "" else StandardCalculator.format(StandardCalculator.evaluate(expr))
        } catch (e: Exception) {
            ""
        }
        _state.update { it.copy(expression = expr, preview = preview) }
    }

    fun onDigit(digit: String) = setExpression(current() + digit)

    fun onOperator(op: String) {
        val expr = current()
        when {
            expr.isEmpty() -> if (op == "−") setExpression(op) // allow a leading minus
            expr.last() in operators -> setExpression(expr.dropLast(1) + op)
            else -> setExpression(expr + op)
        }
    }

    fun onDecimal() {
        val expr = current()
        val segment = expr.takeLastWhile { it !in operators }
        when {
            segment.isEmpty() -> setExpression(expr + "0.")
            segment.contains('.') -> Unit
            else -> setExpression(expr + ".")
        }
    }

    fun onPercent() {
        val expr = current()
        val segment = expr.takeLastWhile { it !in operators }
        val number = segment.toDoubleOrNull() ?: return
        val replaced = expr.dropLast(segment.length) + StandardCalculator.format(number / 100.0)
        setExpression(replaced)
    }

    fun onToggleSign() {
        val expr = current()
        var i = expr.length
        while (i > 0 && (expr[i - 1].isDigit() || expr[i - 1] == '.')) i--
        if (i == expr.length) return // no trailing number
        val before = expr.substring(0, i)
        val number = expr.substring(i)
        if (before.endsWith("−") && (before.length == 1 || before[before.length - 2] in operators)) {
            setExpression(before.dropLast(1) + number)
        } else if (before.isEmpty() || before.last() in operators) {
            setExpression(before + "−" + number)
        }
    }

    fun onBackspace() = setExpression(current().dropLast(1))

    fun onClear() = setExpression("")

    fun onEquals() {
        val preview = _state.value.preview
        if (preview.isNotBlank()) {
            _state.update { it.copy(expression = preview, preview = "") }
        }
    }
}
