package com.fabio.brainnote.ui.screens.cluster

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.usecase.NoteUseCases
import com.fabio.brainnote.domain.usecase.link.DeleteNoteLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClusterViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val deleteNoteLink: DeleteNoteLinkUseCase, // Injected Use Case
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val rootNoteId: Long = checkNotNull(savedStateHandle["rootNoteId"])

    private val _uiState = MutableStateFlow(ClusterUiState())
    val uiState: StateFlow<ClusterUiState> = _uiState.asStateFlow()

    init {
        observeCluster()
    }

    private fun observeCluster() {
        viewModelScope.launch(Dispatchers.IO) {
            noteUseCases.getEntireCluster(rootNoteId).collect { notes ->
                if (notes.size <= 1 && !_uiState.value.isLoading) {
                    _uiState.update { it.copy(isDissolved = true) }
                    return@collect
                }

                val rootTitle = notes.find { it.id == rootNoteId }?.title
                    ?: notes.firstOrNull()?.title
                    ?: ""
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rootNoteTitle = rootTitle,
                        notes = notes
                    )
                }
            }
        }
    }

    fun onNoteLongPressed(noteId: Long) {
        val note = _uiState.value.notes.find { it.id == noteId }
        if (note != null) {
            _uiState.update { it.copy(selectedNoteForUnlink = note) }
        }
    }

    fun dismissUnlinkDialog() {
        _uiState.update { it.copy(selectedNoteForUnlink = null) }
    }

    fun confirmUnlink() {
        val targetNote = _uiState.value.selectedNoteForUnlink ?: return
        dismissUnlinkDialog()

        viewModelScope.launch(Dispatchers.IO) {
            if (targetNote.id == rootNoteId) {
                val childrenIds = _uiState.value.notes
                    .map { it.id }
                    .filter { it != rootNoteId }

                deleteNoteLink.dissolveCluster(rootNoteId = rootNoteId, childrenIds = childrenIds)

                _uiState.update { it.copy(isDissolved = true) }
            } else {
                deleteNoteLink.unlinkSingle(noteId = rootNoteId, linkedToId = targetNote.id)
            }
        }
    }
}