package com.example.notemoon.notes.presentation.util

import androidx.compose.ui.graphics.Color

/**
 * Per-note accent colours used to give the notes wall a lively, Google-Keep-like
 * look. A stable hue is derived from the note id, then blended at low alpha over
 * the theme surface in [com.example.notemoon.notes.presentation.notes.components.NoteItem]
 * so cards stay readable in both light and dark themes.
 */
object NoteColors {
    val hues: List<Color> = listOf(
        Color(0xFFEF5350), // red
        Color(0xFFEC407A), // pink
        Color(0xFFAB47BC), // purple
        Color(0xFF5C6BC0), // indigo
        Color(0xFF29B6F6), // light blue
        Color(0xFF26A69A), // teal
        Color(0xFF66BB6A), // green
        Color(0xFFFFCA28), // amber
        Color(0xFFFF7043)  // deep orange
    )

    /** A deterministic hue for the given note id. */
    fun hueFor(id: Long): Color {
        val size = hues.size
        val index = ((id % size) + size) % size
        return hues[index.toInt()]
    }
}
