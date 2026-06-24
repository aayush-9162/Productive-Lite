package com.example.notemoon.tools.domain.calculator

/** Result of the difference between two dates. */
data class DateDifferenceResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Int
)
