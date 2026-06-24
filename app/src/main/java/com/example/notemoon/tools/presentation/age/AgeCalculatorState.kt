package com.example.notemoon.tools.presentation.age

import com.example.notemoon.tools.domain.calculator.AgeResult

/** UI state for the age calculator. */
data class AgeCalculatorState(
    val birthDate: Long? = null,
    val result: AgeResult? = null,
    val error: String? = null
)
