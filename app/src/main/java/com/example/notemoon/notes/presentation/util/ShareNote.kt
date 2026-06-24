package com.example.notemoon.notes.presentation.util

import android.content.Context
import android.content.Intent
import com.example.notemoon.notes.domain.model.Note

/**
 * Launches the system share sheet (ACTION_SEND) with the note's title and
 * content as plain text. Satisfies the "Share Note" requirement.
 */
fun shareNote(context: Context, note: Note) {
    val text = buildString {
        if (note.title.isNotBlank()) {
            append(note.title)
            append("\n\n")
        }
        append(note.content)
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, note.title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share note"))
}
