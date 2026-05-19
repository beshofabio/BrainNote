package com.fabio.brainnote.ui.screens.noteedit.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    isPinned: Boolean,
    onBackClick: () -> Unit,
    onTogglePin: () -> Unit,
    onSaveClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onSurface)
            }
        },
        actions = {
            IconButton(onClick = onTogglePin) {
                Icon(
                    imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                    contentDescription = "Pin Note",
                    tint = if (isPinned) colorScheme.primary else colorScheme.onSurface
                )
            }
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Default.Check, contentDescription = "Save Note", tint = colorScheme.onSurface)
            }
            IconButton(onClick = onOptionsClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = colorScheme.onSurface)
            }
        },
        title = {}
    )
}