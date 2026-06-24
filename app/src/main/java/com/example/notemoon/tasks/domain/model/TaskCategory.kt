package com.example.notemoon.tasks.domain.model

/**
 * Task categories. A category is stored as a free [String] so users can supply
 * their own ("Custom"), while a set of [presets] is offered in the UI.
 */
object TaskCategory {
    const val PERSONAL = "Personal"
    const val WORK = "Work"
    const val STUDY = "Study"
    const val HEALTH = "Health"

    /** Built-in categories shown in the category dropdown. */
    val presets: List<String> = listOf(PERSONAL, WORK, STUDY, HEALTH)

    /** Label used in the dropdown to enter a category that is not a preset. */
    const val CUSTOM_LABEL = "Custom"

    const val DEFAULT = PERSONAL
}
