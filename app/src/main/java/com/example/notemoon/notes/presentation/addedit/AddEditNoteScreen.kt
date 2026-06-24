package com.example.notemoon.notes.presentation.addedit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val richTextState = rememberRichTextState()
    var initialized by remember { mutableStateOf(false) }

    // Load the existing note's HTML into the editor exactly once.
    LaunchedEffect(state.isLoaded) {
        if (state.isLoaded && !initialized) {
            richTextState.setHtml(state.content)
            initialized = true
        }
    }

    // Push editor changes to the ViewModel (which auto-saves) once initialised.
    LaunchedEffect(initialized) {
        if (!initialized) return@LaunchedEffect
        snapshotFlow { richTextState.annotatedString }
            .drop(1)
            .collect { viewModel.onContentChange(richTextState.toHtml()) }
    }

    // Save the final edit even after this screen (and its viewModelScope) is gone.
    DisposableEffect(Unit) {
        onDispose { viewModel.flush() }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddEditUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { SaveStatusLabel(state.saveState) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onTogglePin) {
                        Icon(
                            imageVector = if (state.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (state.isPinned) "Unpin" else "Pin",
                            tint = if (state.isPinned) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = viewModel::onToggleFavorite) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (state.isFavorite) "Unfavorite" else "Favorite",
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Title", style = MaterialTheme.typography.headlineSmall)
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            FormattingToolbar(richTextState = richTextState)
            HorizontalDivider()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BasicRichTextEditor(
                    state = richTextState,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
                if (richTextState.annotatedString.text.isEmpty()) {
                    Text(
                        text = "Start writing…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveStatusLabel(saveState: SaveState) {
    val text = when (saveState) {
        SaveState.Saving -> "Saving…"
        SaveState.Saved -> "Saved"
        SaveState.Idle -> ""
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun FormattingToolbar(richTextState: RichTextState) {
    val span = richTextState.currentSpanStyle

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        FormatToggle(
            icon = Icons.Filled.FormatBold,
            description = "Bold",
            checked = span.fontWeight == FontWeight.Bold,
            onToggle = { richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) }
        )
        FormatToggle(
            icon = Icons.Filled.FormatItalic,
            description = "Italic",
            checked = span.fontStyle == FontStyle.Italic,
            onToggle = { richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) }
        )
        FormatToggle(
            icon = Icons.Filled.FormatUnderlined,
            description = "Underline",
            checked = span.textDecoration?.contains(TextDecoration.Underline) == true,
            onToggle = { richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) }
        )
        FormatToggle(
            icon = Icons.Filled.FormatStrikethrough,
            description = "Strikethrough",
            checked = span.textDecoration?.contains(TextDecoration.LineThrough) == true,
            onToggle = { richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) }
        )
        FormatToggle(
            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
            description = "Bulleted list",
            checked = richTextState.isUnorderedList,
            onToggle = { richTextState.toggleUnorderedList() }
        )
    }
}

@Composable
private fun FormatToggle(
    icon: ImageVector,
    description: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = { onToggle() },
        modifier = Modifier.padding(end = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
