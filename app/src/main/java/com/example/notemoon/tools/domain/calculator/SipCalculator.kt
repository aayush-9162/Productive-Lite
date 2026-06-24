package com.example.notemoon.tools.domain.calculator

import kotlin.math.pow

/**
 * Projects the future value of a monthly SIP:
 *
 *   FV = P · [((1 + i)^n − 1) / i] · (1 + i)
 *
 * where P is the monthly investment, i the monthly return rate and n the number
 * of months. A zero rate degrades to simple accumulation (P · n).
 */
object SipCalculator {

    /**
     * @param monthlyInvestment amount invested each month (> 0)
     * @param annualRatePercent expected annual return in percent (>= 0)
     * @param months investment duration in months (> 0)
     */
    fun calculate(monthlyInvestment: Double, annualRatePercent: Double, months: Int): SipResult {
        require(monthlyInvestment > 0) { "Enter a monthly amount greater than 0." }
        require(annualRatePercent >= 0) { "Return rate can't be negative." }
        require(months > 0) { "Enter a duration greater than 0." }

        val i = annualRatePercent / 12.0 / 100.0
        val futureValue = if (i == 0.0) {
            monthlyInvestment * months
        } else {
            monthlyInvestment * (((1 + i).pow(months) - 1) / i) * (1 + i)
        }

        val invested = monthlyInvestment * months
        return SipResult(
            investedAmount = invested,
            estimatedReturns = futureValue - invested,
            totalValue = futureValue
        )
    }
}
