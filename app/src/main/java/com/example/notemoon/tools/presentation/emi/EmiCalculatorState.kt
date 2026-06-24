package com.example.notemoon.tools.presentation.emi

import com.example.notemoon.tools.domain.calculator.EmiResult

/** UI state for the EMI calculator. Inputs are kept as raw strings so the text
 *  fields stay user-friendly; [result] is recomputed when they're all valid. */
data class EmiCalculatorState(
    val amount: String = "",
    val rate: String = "",
    val tenure: String = "",
    val tenureInYears: Boolean = true,
    val result: EmiResult? = null,
    val error: String? = null
)
