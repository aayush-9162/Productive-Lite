package com.example.notemoon.tools.domain.calculator

import java.util.Calendar
import java.util.TimeZone

/**
 * Computes the difference between two UTC-midnight dates as years/months/days
 * plus totals. The earlier date is always treated as the start, so the result
 * is non-negative regardless of input order.
 */
object DateDifferenceCalculator {

    private const val DAY_MS = 86_400_000L

    private fun utc(millis: Long): Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }

    fun calculate(dateA: Long, dateB: Long): DateDifferenceResult {
        val startMillis = minOf(dateA, dateB)
        val endMillis = maxOf(dateA, dateB)

        val start = utc(startMillis)
        val end = utc(endMillis)

        var years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR)
        var months = end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
        var days = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months -= 1
            val prevMonth = utc(endMillis).apply { add(Calendar.MONTH, -1) }
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years -= 1
            months += 12
        }

        val totalDays = (endMillis - startMillis) / DAY_MS
        return DateDifferenceResult(
            years = years,
            months = months,
            days = days,
            totalDays = totalDays,
            totalWeeks = totalDays / 7,
            totalMonths = years * 12 + months
        )
    }
}
