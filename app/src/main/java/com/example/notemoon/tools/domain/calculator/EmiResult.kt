package com.example.notemoon.tools.domain.calculator

/** The result of an EMI (equated monthly instalment) calculation. */
data class EmiResult(
    val emi: Double,
    val principal: Double,
    val totalInterest: Double,
    val totalPayment: Double
)
