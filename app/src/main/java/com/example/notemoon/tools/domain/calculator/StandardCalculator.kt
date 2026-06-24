package com.example.notemoon.tools.domain.calculator

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Evaluator for the standard calculator. Parses an arithmetic expression with
 * correct operator precedence (× ÷ before + −) and unary minus, via a small
 * recursive-descent parser. Display operators (× ÷ −) are normalised to ASCII.
 */
object StandardCalculator {

    /** Evaluates [displayExpression]; throws [IllegalArgumentException] if malformed. */
    fun evaluate(displayExpression: String): Double {
        val normalized = displayExpression
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
            .trim()
        if (normalized.isEmpty()) throw IllegalArgumentException("Empty expression")
        val result = Parser(normalized).parse()
        if (!result.isFinite()) throw IllegalArgumentException("Math error")
        return result
    }

    /** Formats a result: integers without a decimal point, otherwise up to 10 dp trimmed. */
    fun format(value: Double): String {
        if (!value.isFinite()) return "Error"
        val bd = BigDecimal(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        return if (bd.scale() <= 0) bd.toBigInteger().toString() else bd.toPlainString()
    }

    private class Parser(private val s: String) {
        private var pos = 0

        fun parse(): Double {
            val value = parseExpression()
            skipSpaces()
            if (pos < s.length) throw IllegalArgumentException("Unexpected '${s[pos]}'")
            return value
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                skipSpaces()
                when (peek()) {
                    '+' -> { pos++; value += parseTerm() }
                    '-' -> { pos++; value -= parseTerm() }
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (true) {
                skipSpaces()
                when (peek()) {
                    '*' -> { pos++; value *= parseFactor() }
                    '/' -> { pos++; value /= parseFactor() }
                    else -> return value
                }
            }
        }

        private fun parseFactor(): Double {
            skipSpaces()
            return when (peek()) {
                '-' -> { pos++; -parseFactor() }
                '+' -> { pos++; parseFactor() }
                else -> parseNumber()
            }
        }

        private fun parseNumber(): Double {
            skipSpaces()
            val start = pos
            while (peek()?.let { it.isDigit() || it == '.' } == true) pos++
            if (pos == start) throw IllegalArgumentException("Number expected")
            return s.substring(start, pos).toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number")
        }

        private fun peek(): Char? = s.getOrNull(pos)
        private fun skipSpaces() { while (peek() == ' ') pos++ }
    }
}
