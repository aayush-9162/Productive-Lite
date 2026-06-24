package com.example.notemoon.tools.presentation.datediff

import com.example.notemoon.tools.domain.calculator.DateDifferenceResult

/** UI state for the date difference calculator. */
data class DateDifferenceState(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val result: DateDifferenceResult? = null
)
