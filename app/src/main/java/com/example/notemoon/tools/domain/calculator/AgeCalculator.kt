package com.example.notemoon.tools.domain.calculator

import java.util.Calendar
import java.util.TimeZone

/**
 * Computes a person's age from a birth date. Dates are UTC-midnight epoch millis
 * (as reported by the Material date picker).
 */
object AgeCalculator {

    private const val DAY_MS = 86_400_000L

    private fun utc(millis: Long): Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }

    /** @throws IllegalArgumentException if [birthMillis] is after [nowMillis]. */
    fun calculate(birthMillis: Long, nowMillis: Long): AgeResult {
        require(birthMillis <= nowMillis) { "Birth date can't be in the future." }

        val birth = utc(birthMillis)
        val now = utc(nowMillis)

        var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        var days = now.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months -= 1
            val prevMonth = utc(nowMillis).apply { add(Calendar.MONTH, -1) }
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years -= 1
            months += 12
        }

        val totalDays = (nowMillis - birthMillis) / DAY_MS

        // Next birthday (this year, or next year if it has already passed).
        val nextBirthday = utc(birthMillis).apply {
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            if (timeInMillis < nowMillis) add(Calendar.YEAR, 1)
        }
        val daysUntilNext = ((nextBirthday.timeInMillis - nowMillis) / DAY_MS).toInt()

        return AgeResult(
            years = years,
            months = months,
            days = days,
            totalDays = totalDays,
            totalWeeks = totalDays / 7,
            totalMonths = years * 12 + months,
            daysUntilNextBirthday = daysUntilNext
        )
    }
}
