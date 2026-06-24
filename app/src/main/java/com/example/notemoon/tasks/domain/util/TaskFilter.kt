package com.example.notemoon.tasks.domain.util

import com.example.notemoon.tasks.domain.model.Priority

/**
 * Filter applied to the task list. [status] narrows by completion, while the
 * optional [priority] and [category] narrow further. Nulls mean "any".
 */
data class TaskFilter(
    val status: StatusFilter = StatusFilter.ALL,
    val priority: Priority? = null,
    val category: String? = null
) {
    val isActive: Boolean
        get() = status != StatusFilter.ALL || priority != null || category != null
}

enum class StatusFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}
