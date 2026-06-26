package com.fabio.brainnote.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.domain.model.Note

@Composable
fun LinkableNoteGridItem(
    modifier: Modifier = Modifier,
    note: Note,
    isLinkingMode: Boolean,
    isRootNote: Boolean,
    isSelectedForLinking: Boolean,
    hasSelectedCluster: Boolean,
    onClick: (Long) -> Unit,
    onLongPress: (Long) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val isCluster = note.linkedNotes.isNotEmpty()

    val isBlocked = isLinkingMode && isCluster && !isRootNote && !isSelectedForLinking && hasSelectedCluster

    val borderColor by animateColorAsState(
        targetValue = when {
            isRootNote -> colorScheme.primary
            isSelectedForLinking -> colorScheme.primary.copy(alpha = 0.7f)
            isBlocked -> colorScheme.error.copy(alpha = 0.4f)
            isLinkingMode -> colorScheme.outlineVariant
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .border(
                width = if (isRootNote || isSelectedForLinking) 2.dp
                else if (isLinkingMode) 1.dp
                else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        if (isCluster) {
            ClusterNoteCard(
                note = note,
                onClick = if (isBlocked) { _ -> } else onClick,
                onLongPress = null
            )
        } else {
            NoteCard(
                note = note,
                onClick = onClick,
                onLongPress = onLongPress
            )
        }

        if (isBlocked) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp),
                color = colorScheme.error,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = "Cannot select another cluster",
                    tint = colorScheme.onError,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (isLinkingMode && !isBlocked && (isSelectedForLinking || isRootNote)) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp),
                color = if (isRootNote) colorScheme.primary else colorScheme.primary.copy(alpha = 0.85f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}