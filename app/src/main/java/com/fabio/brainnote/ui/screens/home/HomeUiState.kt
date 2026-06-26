package com.fabio.brainnote.ui.screens.home

import androidx.compose.runtime.Immutable
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.Note
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: ImmutableList<Category> = persistentListOf(),
    val filteredNotes: ImmutableList<Note> = persistentListOf(),
    val selectedCategoryId: Long = 0L,
    val errorMessage: String? = null,
    val searchQuery: String = "",

    val isLinkingMode: Boolean = false,
    val linkingRootNoteId: Long? = null,
    val selectedForLinking: Set<Long> = emptySet(),

    val hasSelectedCluster: Boolean = false
)