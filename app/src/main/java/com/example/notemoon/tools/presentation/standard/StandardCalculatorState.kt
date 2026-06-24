package com.example.notemoon.tools.presentation.standard

/** UI state for the standard calculator. [preview] is the live evaluation of
 *  [expression], or blank when the expression is incomplete/invalid. */
data class StandardCalculatorState(
    val expression: String = "",
    val preview: String = ""
)
