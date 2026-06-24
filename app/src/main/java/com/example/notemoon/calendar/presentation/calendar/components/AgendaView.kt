package com.example.notemoon.calendar.presentation.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notemoon.calendar.domain.model.CalendarItem
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.calendar.CalendarUiState

/** Agenda: Today, Tomorrow and Upcoming sections, each mixing events and tasks. */
@Composable
fun AgendaView(
    state: CalendarUiState,
    onEventClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = state.today
    val tomorrow = CalendarDateUtils.addDays(today, 1)
    val horizon = CalendarDateUtils.addDays(today, 60)

    val todayItems = state.itemsFor(today).toSortedItems()
    val tomorrowItems = state.itemsFor(tomorrow).toSortedItems()
    val upcomingDays = state.itemsByDay.keys
        .filter { it > tomorrow && it <= horizon && !state.itemsFor(it).isEmpty }
        .sorted()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        section("Today", CalendarDateUtils.fullDate(today))
        itemsOrEmpty(todayItems, "Nothing scheduled today.", onEventClick, onTaskClick)

        section("Tomorrow", CalendarDateUtils.fullDate(tomorrow))
        itemsOrEmpty(tomorrowItems, "Nothing scheduled tomorrow.", onEventClick, onTaskClick)

        section("Upcoming", null)
        if (upcomingDays.isEmpty()) {
            item { EmptyCalendarState("Nothing upcoming in the next 60 days.") }
        } else {
            upcomingDays.forEach { day ->
                item {
                    Text(
                        text = CalendarDateUtils.fullDate(day),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                val dayItems = state.itemsFor(day).toSortedItems()
                items(dayItems.size) { index ->
                    AgendaItem(dayItems[index], onEventClick, onTaskClick)
                }
            }
        }
    }
}

private fun LazyListScope.section(title: String, subtitle: String?) {
    item {
        Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun LazyListScope.itemsOrEmpty(
    items: List<CalendarItem>,
    emptyMessage: String,
    onEventClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    if (items.isEmpty()) {
        item { EmptyCalendarState(emptyMessage) }
    } else {
        items(items.size) { index ->
            AgendaItem(items[index], onEventClick, onTaskClick)
        }
    }
}
