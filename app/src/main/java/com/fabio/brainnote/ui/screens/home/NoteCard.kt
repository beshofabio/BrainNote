package com.fabio.brainnote.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.fakeNote
import com.fabio.brainnote.ui.theme.BrainNoteTheme

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note : Note
) {

}


@Preview(showBackground = false)
@Composable
fun NoteCardPreview(

) {
    BrainNoteTheme {
        NoteCard(
            modifier = Modifier,
            note = fakeNote
        )
    }
}