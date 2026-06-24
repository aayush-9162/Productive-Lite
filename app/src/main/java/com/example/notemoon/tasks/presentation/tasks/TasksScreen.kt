package com.example.notemoon.tasks.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notemoon.tasks.domain.util.SortDirection
import com.example.notemoon.tasks.domain.util.TaskSort
import com.example.notemoon.tasks.domain.util.TaskSortType
import com.example.notemoon.tasks.presentation.tasks.components.StatisticsSection
import com.example.notemoon.tasks.presentation.tasks.components.TaskFilterSheet
import com.example.notemoon.tasks.presentation.tasks.components.TaskItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    onOpenTask: (Long) -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tasks", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.setFilterSheetVisible(true) }) {
                        Icon(
                            imageVector = Icons.Filled.FilterAlt,
                            contentDescription = "Filter tasks",
                            tint = if (state.filter.isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = { viewModel.setSortMenuVisible(true) }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort tasks")
                        }
                        SortMenu(
                            expanded = state.isSortMenuVisible,
                            sort = state.sort,
                            onDismiss = { viewModel.setSortMenuVisible(false) },
                            onSortChange = viewModel::onSortChange
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New task") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = onAddTask
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StatisticsSection(
                statistics = state.statistics,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            SearchField(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            if (state.tasks.isEmpty() && !state.isLoading) {
                EmptyState(
                    isSearching = state.searchQuery.isNotBlank() || state.filter.isActive,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = state.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onOpenTask(task.id) },
                            onToggleComplete = { viewModel.toggleComplete(task) },
                            onDelete = {
                                viewModel.deleteTask(task)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Task deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.restoreTask()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.isFilterSheetVisible) {
        TaskFilterSheet(
            currentFilter = state.filter,
            onApply = {
                viewModel.onFilterChange(it)
                viewModel.setFilterSheetVisible(false)
            },
            onDismiss = { viewModel.setFilterSheetVisible(false) }
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        placeholder = { Text("Search tasks") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    )
}

@Composable
private fun SortMenu(
    expanded: Boolean,
    sort: TaskSort,
    onDismiss: () -> Unit,
    onSortChange: (TaskSort) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        TaskSortType.entries.forEach { type ->
            SortRow(
                label = type.label,
                selected = sort.type == type,
                onClick = { onSortChange(sort.copy(type = type)) }
            )
        }
        HorizontalDivider()
        SortRow(
            label = "Ascending",
            selected = sort.direction == SortDirection.ASCENDING,
            onClick = { onSortChange(sort.copy(direction = SortDirection.ASCENDING)) }
        )
        SortRow(
            label = "Descending",
            selected = sort.direction == SortDirection.DESCENDING,
            onClick = { onSortChange(sort.copy(direction = SortDirection.DESCENDING)) }
        )
    }
}

@Composable
private fun SortRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        trailingIcon = {
            if (selected) Icon(Icons.Filled.Check, contentDescription = "Selected")
        }
    )
}

@Composable
private fun EmptyState(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Text(
                text = if (isSearching) "No tasks match your filters." else "No tasks yet.\nTap \"New task\" to add one.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
