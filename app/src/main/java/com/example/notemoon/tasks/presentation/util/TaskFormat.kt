package com.example.notemoon.tasks.presentation.util

import androidx.compose.ui.graphics.Color
import com.example.notemoon.tasks.domain.model.Priority
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.util.combineDateAndTime
import com.example.notemoon.tasks.domain.util.dueTimeHour
import com.example.notemoon.tasks.domain.util.dueTimeMinute
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val PriorityHigh = Color(0xFFE53935)
private val PriorityMedium = Color(0xFFFB8C00)
private val PriorityLow = Color(0xFF43A047)

/** A distinct colour per priority for the priority indicator. */
fun priorityColor(priority: Priority): Color = when (priority) {
    Priority.HIGH -> PriorityHigh
    Priority.MEDIUM -> PriorityMedium
    Priority.LOW -> PriorityLow
}

/** "HH:mm" for a stored dueTime (minutes since midnight). */
fun formatDueTime(dueTime: Long): String =
    String.format(Locale.getDefault(), "%02d:%02d", dueTimeHour(dueTime), dueTimeMinute(dueTime))

/** The date part of a dueDate, formatted in UTC so the picked day never shifts. */
fun formatDueDate(dueDate: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(dueDate)
}

/** Combined "dd MMM yyyy • HH:mm", or "No due date" when none is set. */
fun formatDueDateTime(task: Task): String {
    if (!task.hasDueDate) return "No due date"
    return "${formatDueDate(task.dueDate)} • ${formatDueTime(task.dueTime)}"
}

/** True when a pending task's due moment is in the past. */
fun isOverdue(task: Task): Boolean {
    if (!task.hasDueDate || task.isCompleted) return false
    return combineDateAndTime(task.dueDate, task.dueTime) < System.currentTimeMillis()
}

/** Formats the day shown in the date-picker field ("Pick a date" when unset). */
fun dueDateFieldLabel(dueDate: Long): String =
    if (dueDate > 0L) formatDueDate(dueDate) else "Pick a date"

/** Today's UTC midnight, a sensible default selection for the date picker. */
fun todayUtcMidnight(): Long {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
