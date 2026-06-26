package com.fabio.brainnote.ui.screens.cluster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.ui.screens.home.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClusterScreen(
    uiState: ClusterUiState,
    rootNoteId: Long,
    onBackClick: () -> Unit,
    dismissUnlinkDialog: () -> Unit,
    onConfirmUnlink: () -> Unit,
    onNoteLongPressed:(Long) -> Unit,
    onNoteClick: (Long) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Handle Pattern 1 Pop-back navigation trigger
    LaunchedEffect(uiState.isDissolved) {
        if (uiState.isDissolved) {
            onBackClick()
        }
    }

    // Contextual Unlinking/Dissolving dialog
    uiState.selectedNoteForUnlink?.let { note ->
        val isRoot = note.id == rootNoteId
        AlertDialog(
            onDismissRequest = dismissUnlinkDialog,
            title = {
                Text(text = if (isRoot) "Dissolve Cluster" else "Unlink Note")
            },
            text = {
                Text(
                    text = if (isRoot)
                        "Unlinking the primary parent note will completely break down this cluster card. All nested notes will become separate individual entries. Do you want to proceed?"
                    else
                        "Are you sure you want to remove '${note.title}' from this cluster group?"
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmUnlink) {
                    Text(text = if (isRoot) "Dissolve All" else "Unlink", color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = dismissUnlinkDialog) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        // Breadcrumb label above title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schema,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Cluster",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = uiState.rootNoteTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            if (uiState.isLoading) {
                // Handle shimmer loading layout placeholder if necessary
            } else {
                when {
                    uiState.notes.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notes in this cluster.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    else -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 80.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp
                        ) {
                            items(uiState.notes, key = { it.id }) { note ->
                                NoteCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    note = note,
                                    onClick = onNoteClick,
                                    onLongPress = onNoteLongPressed // Triggers the context dialog for any card long-pressed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}