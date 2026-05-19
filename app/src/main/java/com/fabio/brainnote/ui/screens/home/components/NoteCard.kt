package com.fabio.brainnote.ui.screens.home.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fabio.brainnote.domain.helper.formatToDate
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.fakeNote
import com.fabio.brainnote.ui.components.CircleCheckbox
import com.fabio.brainnote.ui.components.VoiceNotePlayer
import com.fabio.brainnote.ui.theme.BrainNoteTheme

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: ((Long) -> Unit)? = null
) {
    val noteCheckList = note.checklist
    val noteVoiceNotes = note.voiceNotes
    val colorScheme = MaterialTheme.colorScheme

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        onClick = {
            Log.d("NoteClick", "NoteCard: ${note.id}")
            onClick?.invoke(note.id)
        },
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Column(
             modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            note.imagePath?.let {
                AsyncImage(
                    model = note.imagePath,
                    contentDescription = "Note Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onPrimary.copy(alpha = 0.7f),
                    maxLines = 3
                )

                if (noteCheckList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        noteCheckList.take(3).forEach { item ->
                            CircleCheckbox(
                                title = item.text,
                                selected = item.isChecked,
                                onChecked = {}
                            )
                        }
                    }
                }

                noteVoiceNotes.forEach { voice ->
                    VoiceNotePlayer(durationSeconds = voice.duration)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = note.createdAt.formatToDate(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onPrimary.copy(alpha = 0.5f)
                    )

                    if (note.reminders.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder set",
                                modifier = Modifier.size(12.dp),
                                tint = colorScheme.onPrimary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun NoteCardPreview() {
    BrainNoteTheme {
        NoteCard(
            modifier = Modifier,
            note = fakeNote
        )
    }
}