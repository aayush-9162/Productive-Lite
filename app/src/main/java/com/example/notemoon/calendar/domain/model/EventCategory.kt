package com.example.notemoon.calendar.domain.model

/**
 * Event categories. Stored as a free [String] so users can supply a custom one,
 * while [presets] are offered in the category dropdown.
 */
object EventCategory {
    const val PERSONAL = "Personal"
    const val WORK = "Work"
    const val STUDY = "Study"
    const val MEETING = "Meeting"
    const val HEALTH = "Health"

    val presets: List<String> = listOf(PERSONAL, WORK, STUDY, MEETING, HEALTH)

    const val CUSTOM_LABEL = "Custom"
    const val DEFAULT = PERSONAL
}
