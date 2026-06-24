package com.example.notemoon.notes.presentation.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Unarchive
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notemoon.notes.domain.model.Note
import com.example.notemoon.notes.domain.util.NoteOrder
import com.example.notemoon.notes.domain.util.OrderType
import com.example.notemoon.notes.presentation.notes.components.NoteItem
import com.example.notemoon.notes.presentation.util.shareNote
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onAddNote: () -> Unit,
    onEditNote: (Long) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (state.showArchived) "Archived" else "NoteMoon",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.setShowArchived(!state.showArchived) }) {
                        Icon(
                            imageVector = if (state.showArchived) Icons.Filled.Unarchive else Icons.Filled.Archive,
                            contentDescription = if (state.showArchived) "Show active notes" else "Show archived notes"
                        )
                    }
                    Box {
                        IconButton(onClick = { viewModel.toggleOrderMenu() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort notes"
                            )
                        }
                        SortMenu(
                            expanded = state.isOrderMenuVisible,
                            noteOrder = state.noteOrder,
                            onDismiss = { viewModel.setOrderMenuVisible(false) },
                            onOrderChange = viewModel::onOrderChange
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New note") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = onAddNote
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchField(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            if (state.notes.isEmpty() && !state.isLoading) {
                EmptyState(
                    isSearching = state.searchQuery.isNotBlank(),
                    showArchived = state.showArchived,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                NotesGrid(
                    notes = state.notes,
                    grouped = !state.showArchived && state.searchQuery.isBlank(),
                    onEditNote = onEditNote,
                    onTogglePin = viewModel::togglePin,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onToggleArchive = viewModel::toggleArchive,
                    onShare = { note -> shareNote(context, note) },
                    onDelete = { note ->
                        viewModel.deleteNote(note)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Note deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.restoreNote()
                            }
                        }
                    }
                )
            }
        }
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search notes") },
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
private fun NotesGrid(
    notes: List<Note>,
    grouped: Boolean,
    onEditNote: (Long) -> Unit,
    onTogglePin: (Note) -> Unit,
    onToggleFavorite: (Note) -> Unit,
    onToggleArchive: (Note) -> Unit,
    onShare: (Note) -> Unit,
    onDelete: (Note) -> Unit
) {
    val pinned = if (grouped) notes.filter { it.isPinned } else emptyList()
    val others = if (grouped) notes.filterNot { it.isPinned } else notes

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (pinned.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) { SectionHeader("Pinned") }
            items(items = pinned, key = { it.id }) { note ->
                NoteCardEntry(note, onEditNote, onTogglePin, onToggleFavorite, onToggleArchive, onShare, onDelete)
            }
            if (others.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) { SectionHeader("Others") }
            }
        }
        items(items = others, key = { it.id }) { note ->
            NoteCardEntry(note, onEditNote, onTogglePin, onToggleFavorite, onToggleArchive, onShare, onDelete)
        }
    }
}

@Composable
private fun NoteCardEntry(
    note: Note,
    onEditNote: (Long) -> Unit,
    onTogglePin: (Note) -> Unit,
    onToggleFavorite: (Note) -> Unit,
    onToggleArchive: (Note) -> Unit,
    onShare: (Note) -> Unit,
    onDelete: (Note) -> Unit
) {
    NoteItem(
        note = note,
        onClick = { onEditNote(note.id) },
        onTogglePin = { onTogglePin(note) },
        onToggleFavorite = { onToggleFavorite(note) },
        onToggleArchive = { onToggleArchive(note) },
        onShare = { onShare(note) },
        onDelete = { onDelete(note) }
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SortMenu(
    expanded: Boolean,
    noteOrder: NoteOrder,
    onDismiss: () -> Unit,
    onOrderChange: (NoteOrder) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        SortFieldItem(
            label = "Title",
            selected = noteOrder is NoteOrder.Title,
            onClick = { onOrderChange(NoteOrder.Title(noteOrder.orderType)) }
        )
        SortFieldItem(
            label = "Date created",
            selected = noteOrder is NoteOrder.DateCreated,
            onClick = { onOrderChange(NoteOrder.DateCreated(noteOrder.orderType)) }
        )
        SortFieldItem(
            label = "Last modified",
            selected = noteOrder is NoteOrder.LastModified,
            onClick = { onOrderChange(NoteOrder.LastModified(noteOrder.orderType)) }
        )
        HorizontalDivider()
        SortFieldItem(
            label = "Ascending",
            selected = noteOrder.orderType == OrderType.Ascending,
            onClick = { onOrderChange(noteOrder.copy(OrderType.Ascending)) }
        )
        SortFieldItem(
            label = "Descending",
            selected = noteOrder.orderType == OrderType.Descending,
            onClick = { onOrderChange(noteOrder.copy(OrderType.Descending)) }
        )
    }
}

@Composable
private fun SortFieldItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        trailingIcon = {
            if (selected) {
                Icon(Icons.Filled.Check, contentDescription = "Selected")
            }
        }
    )
}

@Composable
private fun EmptyState(
    isSearching: Boolean,
    showArchived: Boolean,
    modifier: Modifier = Modifier
) {
    val message = when {
        isSearching -> "No notes match your search."
        showArchived -> "No archived notes."
        else -> "No notes yet.\nTap \"New note\" to create your first one."
    }
    val icon: ImageVector = if (isSearching) Icons.Filled.Search else Icons.AutoMirrored.Filled.NoteAdd

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
