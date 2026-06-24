package com.example.notemoon.tools.domain.calculator

/** The result of an age calculation, broken down several ways. */
data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Int,
    val daysUntilNextBirthday: Int
)
