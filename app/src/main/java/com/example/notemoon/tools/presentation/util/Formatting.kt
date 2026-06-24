package com.example.notemoon.tools.presentation.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val amountFormat = DecimalFormat("#,##0.00")

/** Formats a monetary amount with grouping and two decimals (e.g. 12,345.67). */
fun formatAmount(value: Double): String = amountFormat.format(value)

/** Formats a UTC-midnight date as "dd MMM yyyy". */
fun formatUtcDate(millis: Long): String {
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
    return fmt.format(millis)
}
