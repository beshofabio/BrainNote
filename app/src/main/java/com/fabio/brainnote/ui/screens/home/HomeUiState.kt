package com.fabio.brainnote.ui.screens.home

import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.Note

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val selectedCategoryId: Long = 0L,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)