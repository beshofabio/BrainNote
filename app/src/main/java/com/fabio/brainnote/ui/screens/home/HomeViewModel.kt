package com.fabio.brainnote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.usecase.CategoryUseCases
import com.fabio.brainnote.domain.usecase.NoteUseCases
import com.fabio.brainnote.domain.usecase.link.InsertNoteLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases,
    private val noteUseCases: NoteUseCases,
    private val insertNoteLink: InsertNoteLinkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentCategoryNotes: List<Note> = emptyList()
    private var notesJob: Job? = null

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUseCases.getAll().collect { categories ->
                if (categories.isNotEmpty()) {
                    val currentSelectedId = _uiState.value.selectedCategoryId
                    val activeCategoryId = if (currentSelectedId == 0L) {
                        categories.first().id
                    } else {
                        currentSelectedId
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories.toImmutableList(),
                            selectedCategoryId = activeCategoryId
                        )
                    }
                    observeNotesForCategory(activeCategoryId)
                } else {
                    _uiState.update { it.copy(isLoading = false, categories = persistentListOf()) }
                }
            }
        }
    }

    private fun observeNotesForCategory(categoryId: Long) {
        notesJob?.cancel()
        notesJob = viewModelScope.launch(Dispatchers.IO) {
            noteUseCases.getNotesByCategory(categoryId).collect { notes ->
                currentCategoryNotes = notes
                applySearchFilter()
            }
        }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        observeNotesForCategory(categoryId)
    }

    fun onSearchQueryChanged(searchQuery: String) {
        _uiState.update { it.copy(searchQuery = searchQuery) }
        applySearchFilter()
    }

    private fun applySearchFilter() {
        val query = _uiState.value.searchQuery
        val filteredNotes = if (query.isBlank()) {
            currentCategoryNotes
        } else {
            currentCategoryNotes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }
        val sortedNotes = filteredNotes.sortedWith(
            compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt }
        )
        _uiState.update { it.copy(filteredNotes = sortedNotes.toImmutableList()) }
    }

    fun onNoteLongPress(noteId: Long) {
        if (!_uiState.value.isLinkingMode) {
            _uiState.update {
                it.copy(
                    isLinkingMode = true,
                    linkingRootNoteId = noteId,
                    selectedForLinking = emptySet(),
                    hasSelectedCluster = false
                )
            }
        }
    }

    fun onNoteToggleForLinking(noteId: Long) {
        val state = _uiState.value
        if (noteId == state.linkingRootNoteId) return

        val updated = if (noteId in state.selectedForLinking) {
            state.selectedForLinking - noteId
        } else {
            state.selectedForLinking + noteId
        }

        val hasCluster = updated.any { selectedId ->
            state.filteredNotes.find { it.id == selectedId }?.linkedNotes?.isNotEmpty() == true
        }

        _uiState.update {
            it.copy(
                selectedForLinking = updated,
                hasSelectedCluster = hasCluster
            )
        }
    }

    fun onCancelLinking() {
        _uiState.update {
            it.copy(
                isLinkingMode = false,
                linkingRootNoteId = null,
                selectedForLinking = emptySet(),
                hasSelectedCluster = false
            )
        }
    }

    fun onConfirmLinking() {
        val state = _uiState.value
        val rootId = state.linkingRootNoteId ?: return
        val targets = state.selectedForLinking.toList()

        if (targets.isEmpty()) {
            onCancelLinking()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val existingClusterRoot = targets
                .mapNotNull { targetId -> state.filteredNotes.find { it.id == targetId } }
                .find { it.linkedNotes.isNotEmpty() }

            if (existingClusterRoot != null) {
                insertNoteLink(noteId = existingClusterRoot.id, linkedToId = rootId)
                targets.forEach { targetId ->
                    if (targetId != existingClusterRoot.id) {
                        insertNoteLink(noteId = existingClusterRoot.id, linkedToId = targetId)
                    }
                }
            } else {
                targets.forEach { targetId ->
                    insertNoteLink(noteId = rootId, linkedToId = targetId)
                }
            }
        }

        _uiState.update {
            it.copy(
                isLinkingMode = false,
                linkingRootNoteId = null,
                selectedForLinking = emptySet(),
                hasSelectedCluster = false
            )
        }
    }
}