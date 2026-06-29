package com.example.notemoon.alarm.domain.util

import kotlin.random.Random

/** A single plus/minus problem the user must solve to dismiss an alarm. */
data class MathQuestion(
    val left: Int,
    val right: Int,
    val isPlus: Boolean
) {
    val answer: Int get() = if (isPlus) left + right else left - right
    val prompt: String get() = "$left ${if (isPlus) "+" else "−"} $right"
}

/** Generates fresh random two-digit (10–99) addition/subtraction questions. */
object MathChallenge {

    fun generate(): MathQuestion {
        val plus = Random.nextBoolean()
        return if (plus) {
            MathQuestion(twoDigit(), twoDigit(), isPlus = true)
        } else {
            // Keep the result non-negative with both operands two-digit.
            val a = twoDigit()
            val b = Random.nextInt(10, a + 1)
            MathQuestion(a, b, isPlus = false)
        }
    }

    private fun twoDigit(): Int = Random.nextInt(10, 100)
}
