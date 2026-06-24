package com.example.notemoon.calendar.presentation.util

import androidx.compose.ui.graphics.Color
import com.example.notemoon.calendar.domain.model.EventCategory

/** A stable accent colour per event category (custom categories hash to one). */
fun categoryColor(category: String): Color = when (category) {
    EventCategory.PERSONAL -> Color(0xFF42A5F5) // blue
    EventCategory.WORK -> Color(0xFF7E57C2) // deep purple
    EventCategory.STUDY -> Color(0xFF26A69A) // teal
    EventCategory.MEETING -> Color(0xFFEF5350) // red
    EventCategory.HEALTH -> Color(0xFF66BB6A) // green
    else -> CustomPalette[(category.hashCode().mod(CustomPalette.size))]
}

private val CustomPalette = listOf(
    Color(0xFFEC407A),
    Color(0xFFFF7043),
    Color(0xFFFFCA28),
    Color(0xFF5C6BC0),
    Color(0xFF8D6E63)
)
