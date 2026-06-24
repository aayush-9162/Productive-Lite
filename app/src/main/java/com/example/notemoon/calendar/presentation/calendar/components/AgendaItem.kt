package com.example.notemoon.calendar.presentation.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notemoon.calendar.domain.model.CalendarItem

/**
 * Renders a single agenda/day-list entry, dispatching to [EventCard] for events
 * or [CalendarTaskCard] for tasks.
 */
@Composable
fun AgendaItem(
    item: CalendarItem,
    onEventClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is CalendarItem.EventEntry -> EventCard(
            event = item.event,
            onClick = { onEventClick(item.event.id) },
            modifier = modifier
        )

        is CalendarItem.TaskEntry -> CalendarTaskCard(
            task = item.task,
            onClick = { onTaskClick(item.task.id) },
            modifier = modifier
        )
    }
}
