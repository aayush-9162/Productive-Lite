package com.example.notemoon.calendar.presentation.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.calendar.CalendarUiState

/** Monthly grid with day-cells, followed by the selected day's events and tasks. */
@Composable
fun MonthView(
    state: CalendarUiState,
    onDateSelected: (Long) -> Unit,
    onEventClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = remember(state.displayedMonth) {
        CalendarDateUtils.monthGridDays(state.displayedMonth)
    }
    val selectedItems = state.itemsFor(state.selectedDate).toSortedItems()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CalendarDateUtils.weekdayHeaders.forEach { header ->
                        Text(
                            text = header,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                days.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            CalendarDayCell(
                                day = day,
                                inMonth = CalendarDateUtils.isInMonth(day, state.displayedMonth),
                                isToday = CalendarDateUtils.isSameDay(day, state.today),
                                isSelected = CalendarDateUtils.isSameDay(day, state.selectedDate),
                                dayItems = state.itemsFor(day),
                                onClick = { onDateSelected(day) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = CalendarDateUtils.fullDate(state.selectedDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (selectedItems.isEmpty()) {
            item { EmptyCalendarState("Nothing scheduled on this day.") }
        } else {
            items(selectedItems.size) { index ->
                AgendaItem(
                    item = selectedItems[index],
                    onEventClick = onEventClick,
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}
