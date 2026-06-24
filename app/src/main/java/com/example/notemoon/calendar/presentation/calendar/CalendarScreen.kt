package com.example.notemoon.calendar.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notemoon.calendar.domain.util.CalendarDateUtils
import com.example.notemoon.calendar.presentation.calendar.components.AgendaView
import com.example.notemoon.calendar.presentation.calendar.components.DayView
import com.example.notemoon.calendar.presentation.calendar.components.EmptyCalendarState
import com.example.notemoon.calendar.presentation.calendar.components.EventCard
import com.example.notemoon.calendar.presentation.calendar.components.MonthView
import com.example.notemoon.calendar.presentation.calendar.components.WeekView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onAddEvent: (Long) -> Unit,
    onOpenEvent: (Long) -> Unit,
    onOpenTask: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    rememberCoroutineScope()

    androidx.compose.material3.Scaffold(
        topBar = {
            if (state.isSearchActive) {
                SearchTopBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onClose = { viewModel.setSearchActive(false) }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = viewModel::goToToday) {
                            Icon(Icons.Filled.Today, contentDescription = "Go to today")
                        }
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search events")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!state.isSearchActive) {
                FloatingActionButton(onClick = { onAddEvent(state.selectedDate) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add event")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isSearchActive) {
                SearchResults(state = state, onOpenEvent = onOpenEvent)
            } else {
                TabRow(selectedTabIndex = state.view.ordinal) {
                    CalendarView.entries.forEach { view ->
                        Tab(
                            selected = state.view == view,
                            onClick = { viewModel.onViewChange(view) },
                            text = { Text(view.label) }
                        )
                    }
                }

                if (state.view != CalendarView.AGENDA) {
                    PeriodNavigator(
                        label = periodLabel(state),
                        onPrevious = viewModel::onPrevious,
                        onNext = viewModel::onNext
                    )
                }

                when (state.view) {
                    CalendarView.MONTH -> MonthView(state, viewModel::onDateSelected, onOpenEvent, onOpenTask)
                    CalendarView.WEEK -> WeekView(state, viewModel::onDateSelected, onOpenEvent, onOpenTask)
                    CalendarView.DAY -> DayView(state, onOpenEvent, onOpenTask)
                    CalendarView.AGENDA -> AgendaView(state, onOpenEvent, onOpenTask)
                }
            }
        }
    }
}

private fun periodLabel(state: CalendarUiState): String = when (state.view) {
    CalendarView.MONTH -> CalendarDateUtils.monthTitle(state.displayedMonth)
    CalendarView.WEEK -> "Week of ${CalendarDateUtils.shortDate(CalendarDateUtils.weekDays(state.selectedDate).first())}"
    CalendarView.DAY -> CalendarDateUtils.fullDate(state.selectedDate)
    CalendarView.AGENDA -> ""
}

@Composable
private fun PeriodNavigator(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous")
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search events") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Close search")
            }
        }
    )
}

@Composable
private fun SearchResults(
    state: CalendarUiState,
    onOpenEvent: (Long) -> Unit
) {
    val results = state.searchResults()
    if (state.searchQuery.isBlank()) {
        EmptyCalendarState("Type to search your events.", Modifier.fillMaxSize())
    } else if (results.isEmpty()) {
        EmptyCalendarState("No events match \"${state.searchQuery}\".", Modifier.fillMaxSize())
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(results, key = { it.id }) { event ->
                EventCard(event = event, onClick = { onOpenEvent(event.id) })
            }
        }
    }
}
