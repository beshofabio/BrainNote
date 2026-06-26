package com.fabio.brainnote.ui.screens.cluster

import androidx.compose.runtime.Immutable
import com.fabio.brainnote.domain.model.Note

@Immutable
data class ClusterUiState(
    val isLoading: Boolean = true,
    val rootNoteTitle: String = "",
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null,

    val selectedNoteForUnlink: Note? = null,
    val isDissolved: Boolean = false
)