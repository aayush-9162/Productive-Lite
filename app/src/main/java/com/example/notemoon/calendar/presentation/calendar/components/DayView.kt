package com.example.notemoon.calendar.presentation.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.calendar.CalendarUiState

/** Single-day timeline: the selected day's events and tasks, ordered by time. */
@Composable
fun DayView(
    state: CalendarUiState,
    onEventClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = state.itemsFor(state.selectedDate).toSortedItems()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = CalendarDateUtils.fullDate(state.selectedDate),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (items.isEmpty()) {
            item { EmptyCalendarState("Nothing scheduled on this day.\nTap + to add an event.") }
        } else {
            items(items.size) { index ->
                AgendaItem(
                    item = items[index],
                    onEventClick = onEventClick,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}
