package com.example.notemoon.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.calendar.components.CalendarTaskCard
import com.example.notemoon.calendar.presentation.calendar.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCalendar: () -> Unit,
    onOpenEvent: (Long) -> Unit,
    onOpenTask: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productive Lite", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SummaryCard(
                    today = state.today,
                    eventsToday = state.totalEventsToday,
                    pendingTasksToday = state.pendingTasksToday,
                    onOpenCalendar = onOpenCalendar
                )
            }

            item { SectionTitle("Today's events") }
            if (state.todaysEvents.isEmpty()) {
                item { EmptyHint("No events today.") }
            } else {
                items(state.todaysEvents, key = { "e${it.id}" }) { event ->
                    EventCard(event = event, onClick = { onOpenEvent(event.id) })
                }
            }

            if (state.todaysTasks.isNotEmpty()) {
                item { SectionTitle("Today's tasks") }
                items(state.todaysTasks, key = { "t${it.id}" }) { task ->
                    CalendarTaskCard(task = task, onClick = { onOpenTask(task.id) })
                }
            }

            item { SectionTitle("Upcoming events") }
            if (state.upcomingEvents.isEmpty()) {
                item { EmptyHint("Nothing in the next 30 days.") }
            } else {
                items(state.upcomingEvents, key = { "u${it.id}" }) { event ->
                    EventCard(event = event, onClick = { onOpenEvent(event.id) })
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    today: Long,
    eventsToday: Int,
    pendingTasksToday: Int,
    onOpenCalendar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = CalendarDateUtils.weekdayName(today),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = CalendarDateUtils.fullDate(today),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$eventsToday event(s) • $pendingTasksToday task(s) due today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 8.dp)
            )
            FilledTonalButton(
                onClick = onOpenCalendar,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Open calendar")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun EmptyHint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
