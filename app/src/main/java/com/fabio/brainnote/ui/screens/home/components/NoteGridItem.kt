package com.fabio.brainnote.ui.screens.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fabio.brainnote.domain.model.Note

@Composable
fun NoteGridItem(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Long) -> Unit
) {
    if (note.linkedNotes.isNotEmpty()) {
        ClusterNoteCard(modifier = modifier, note = note, onClick = onClick)
    } else {
        NoteCard(modifier = modifier, note = note, onClick = onClick)
    }
}