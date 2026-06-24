package com.example.notemoon.tools.presentation.sip

import com.example.notemoon.tools.domain.calculator.SipResult

/** UI state for the SIP calculator. */
data class SipCalculatorState(
    val monthlyAmount: String = "",
    val annualReturn: String = "",
    val duration: String = "",
    val durationInYears: Boolean = true,
    val result: SipResult? = null,
    val error: String? = null
)
