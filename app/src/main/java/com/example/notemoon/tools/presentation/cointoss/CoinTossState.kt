package com.example.notemoon.tools.presentation.cointoss

enum class CoinSide(val label: String) { HEADS("Heads"), TAILS("Tails") }

/** UI state for the coin toss tool. */
data class CoinTossState(
    val result: CoinSide? = null,
    val isFlipping: Boolean = false,
    val headsCount: Int = 0,
    val tailsCount: Int = 0
)
