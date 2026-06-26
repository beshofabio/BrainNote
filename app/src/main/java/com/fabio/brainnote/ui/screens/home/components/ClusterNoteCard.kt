package com.fabio.brainnote.ui.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.domain.model.Note

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClusterNoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Long) -> Unit,
    onLongPress: ((Long) -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .padding(top = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.BottomStart
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(40.dp)
                .offset(x = 16.dp, y = (-12).dp)
                .clip(RoundedCornerShape(15.dp))
                .background(colorScheme.surfaceVariant)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(40.dp)
                .offset(x = 8.dp, y = (-6).dp)
                .clip(RoundedCornerShape(15.dp))
                .background(colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .align(Alignment.TopCenter)
        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = { onClick(note.id) },
                    onLongClick = onLongPress?.let { lambda -> { lambda(note.id) } }
                ),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.elevatedCardElevation(6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schema,
                            contentDescription = "Cluster",
                            tint = colorScheme.primary
                        )
                        if (note.isPinned) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                modifier = Modifier.size(16.dp),
                                tint = colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                    Text(
                        text = "${note.linkedNotes.size + 1} Notes",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    note.linkedNotes.take(3).forEach { linkedNote ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(colorScheme.onSurface.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = linkedNote.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                    if (note.linkedNotes.size > 3) {
                        Text(
                            text = "+ ${note.linkedNotes.size - 3} more",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
