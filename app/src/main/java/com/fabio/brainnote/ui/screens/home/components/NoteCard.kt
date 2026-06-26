package com.fabio.brainnote.ui.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fabio.brainnote.domain.helper.formatToDate
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.ui.components.CircleCheckbox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: ((Long) -> Unit)? = null,
    onLongPress: ((Long) -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }

    ElevatedCard(
        modifier = modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = { onClick?.invoke(note.id) },
                onLongClick = { onLongPress?.invoke(note.id) }
            ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Box {
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

                    if (note.checklist.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            note.checklist.take(3).forEach { item ->
                                CircleCheckbox(
                                    title = item.text,
                                    selected = item.isChecked,
                                    onChecked = {}
                                )
                            }
                        }
                    }

                    // Voice note pill — simple, clean, no waveform
                    if (note.voiceNotes.isNotEmpty()) {
                        val totalSeconds = note.voiceNotes.sumOf { it.duration }
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        val label = if (note.voiceNotes.size == 1) {
                            "%d:%02d".format(minutes, seconds)
                        } else {
                            "${note.voiceNotes.size} recordings"
                        }

                        Surface(
                            color = colorScheme.onPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
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

            if (note.isPinned) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(16.dp),
                    tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}