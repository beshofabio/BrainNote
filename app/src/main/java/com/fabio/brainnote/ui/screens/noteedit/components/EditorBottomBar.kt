package com.fabio.brainnote.ui.screens.noteedit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun EditorBottomBar(
    onAddChecklist: () -> Unit,
    onAddImage: () -> Unit,
    onRecordAudio: () -> Unit,
    onAddReminder: () -> Unit
) {
    var showAttachmentMenu by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val plusIconRotation by animateFloatAsState(
        targetValue = if (showAttachmentMenu) 45f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "plus_rotation"
    )

    BottomAppBar(
        containerColor = colorScheme.surfaceVariant,
        contentPadding = PaddingValues(horizontal = 8.dp),
        tonalElevation = 0.dp
    ) {
        IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Attachment",
                tint = if (showAttachmentMenu) colorScheme.primary else colorScheme.onSurfaceVariant,
                modifier = Modifier.rotate(plusIconRotation)
            )
        }

        AnimatedVisibility(
            visible = showAttachmentMenu,
            enter = expandHorizontally(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = shrinkHorizontally(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onAddChecklist(); showAttachmentMenu = false }) {
                    Icon(
                        imageVector = Icons.Default.CheckBox,
                        contentDescription = "Checklist",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onAddImage(); showAttachmentMenu = false }) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Image",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onRecordAudio(); showAttachmentMenu = false }) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Audio",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onAddReminder(); showAttachmentMenu = false }) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Reminder",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Edited just now",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}