package com.example.notemoon.tools.domain.calculator

import kotlin.math.pow

/**
 * Computes the EMI for a reducing-balance loan:
 *
 *   EMI = P · r · (1 + r)^n / ((1 + r)^n − 1)
 *
 * where r is the monthly interest rate and n the number of months. A zero
 * interest rate degrades gracefully to P / n.
 */
object EmiCalculator {

    /**
     * @param principal loan amount (> 0)
     * @param annualRatePercent annual interest rate in percent (>= 0)
     * @param tenureMonths number of monthly instalments (> 0)
     * @throws IllegalArgumentException if inputs are invalid
     */
    fun calculate(principal: Double, annualRatePercent: Double, tenureMonths: Int): EmiResult {
        require(principal > 0) { "Enter a loan amount greater than 0." }
        require(annualRatePercent >= 0) { "Interest rate can't be negative." }
        require(tenureMonths > 0) { "Enter a tenure greater than 0." }

        val monthlyRate = annualRatePercent / 12.0 / 100.0
        val emi = if (monthlyRate == 0.0) {
            principal / tenureMonths
        } else {
            val factor = (1 + monthlyRate).pow(tenureMonths)
            principal * monthlyRate * factor / (factor - 1)
        }

        val totalPayment = emi * tenureMonths
        return EmiResult(
            emi = emi,
            principal = principal,
            totalInterest = totalPayment - principal,
            totalPayment = totalPayment
        )
    }
}
