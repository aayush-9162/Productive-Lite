package com.example.notemoon.tasks.domain.model

/**
 * How often a task recurs. Stored in the database as [name].
 */
enum class RepeatType(val label: String) {
    NONE("Does not repeat"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    companion object {
        fun from(value: String?): RepeatType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NONE
    }
}
