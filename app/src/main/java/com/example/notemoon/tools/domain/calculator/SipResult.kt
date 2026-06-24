package com.example.notemoon.tools.domain.calculator

/** Result of a SIP (systematic investment plan) projection. */
data class SipResult(
    val investedAmount: Double,
    val estimatedReturns: Double,
    val totalValue: Double
)
