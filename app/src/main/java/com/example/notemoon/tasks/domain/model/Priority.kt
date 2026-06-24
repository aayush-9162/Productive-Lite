package com.example.notemoon.tasks.domain.model

/**
 * Task priority levels. Stored in the database as [name]; [from] maps a stored
 * value back to the enum, defaulting to [MEDIUM] for unknown/legacy values.
 */
enum class Priority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    companion object {
        fun from(value: String?): Priority =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
    }
}
