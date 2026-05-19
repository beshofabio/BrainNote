package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.model.VoiceNote
import com.fabio.brainnote.domain.usecase.CategoryUseCases
import com.fabio.brainnote.domain.usecase.MediaUseCases
import com.fabio.brainnote.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val mediaUseCases: MediaUseCases,
    private val categoryUseCases: CategoryUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(NoteEditorState())
    val state: StateFlow<NoteEditorState> = _state.asStateFlow()

    private var currentAudioPath: String? = null
    private var originalCreatedAt: Long? = null

    init {
        observeCategories()
        observePlaybackProgress()

        val noteId = savedStateHandle.get<Long>("noteId")
        if (noteId != null && noteId != -1L) {
            loadExistingNote(noteId)
        }
    }

    private fun observeCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUseCases.getAll().collect { categories ->
                _state.update { it.copy(availableCategories = categories) }

                if (_state.value.noteId == null && _state.value.selectedCategory == null && categories.isNotEmpty()) {
                    _state.update { it.copy(selectedCategory = categories.first()) }
                }
            }
        }
    }

    private fun observePlaybackProgress() {
        viewModelScope.launch {
            mediaUseCases.getPlaybackProgress().collect { progress ->
                _state.update {
                    it.copy(
                        isPlaying = progress.isPlaying,
                        currentPlaybackPosition = progress.currentPosition,
                        totalPlaybackDuration = progress.duration
                    )
                }
            }
        }
    }

    private fun loadExistingNote(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            val note = noteUseCases.getNoteById(noteId)

            if (note != null) {
                originalCreatedAt = note.createdAt

                _state.update {
                    it.copy(
                        noteId = note.id,
                        title = note.title,
                        content = note.content,
                        selectedCategory = note.category,
                        isPinned = note.isPinned,
                        isLocked = note.isLocked,
                        colorPriority = note.colorPriority,
                        imagePath = note.imagePath,
                        checklists = note.checklist,
                        voiceNotes = note.voiceNotes,
                        reminders = note.reminders,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }


    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
    }

    fun onContentChange(newContent: String) {
        _state.update { it.copy(content = newContent) }
    }

    fun onTogglePin() {
        _state.update { it.copy(isPinned = !it.isPinned) }
    }

    fun onAddChecklistItem() {
        val newItem = ChecklistItem(
            text = "",
            isChecked = false,
            position = _state.value.checklists.size
        )
        _state.update { it.copy(checklists = it.checklists + newItem) }
    }

    fun onUpdateChecklistItem(index: Int, newText: String, isChecked: Boolean) {
        val updatedList = _state.value.checklists.toMutableList()
        val currentItem = updatedList[index]
        updatedList[index] = currentItem.copy(text = newText, isChecked = isChecked)

        _state.update { it.copy(checklists = updatedList) }
    }

    fun onRemoveChecklistItem(index: Int) {
        val updatedList = _state.value.checklists.toMutableList()
        updatedList.removeAt(index)
        _state.update { it.copy(checklists = updatedList) }
    }

    fun onAddReminder(reminder: Reminder) {
        _state.update { it.copy(reminders = it.reminders + reminder) }
    }

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value

            if (currentState.title.isBlank() && currentState.content.isBlank()) {
                _state.update { it.copy(errorMessage = "Note cannot be empty!") }
                return@launch
            }

            val noteToSave = Note(
                id = currentState.noteId ?: 0L,
                title = currentState.title,
                content = currentState.content,
                category = currentState.selectedCategory,
                colorPriority = currentState.colorPriority,
                isPinned = currentState.isPinned,
                isLocked = currentState.isLocked,
                createdAt = originalCreatedAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                imagePath = currentState.imagePath,
                checklist = currentState.checklists,
                voiceNotes = currentState.voiceNotes,
                reminders = currentState.reminders,
                linkedNotes = emptyList()
            )

            noteUseCases.saveFullNote(noteToSave)
            _state.update { it.copy(isSaved = true) }
        }
    }

    fun onCategorySelected(categoryId: Long) {
        val category = _state.value.availableCategories.find { it.id == categoryId }
        _state.update { it.copy(selectedCategory = category) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onImageConfirmed(uri: Uri) {
        viewModelScope.launch {
            val savedPath = mediaUseCases.saveImage(uri.toString())
            if (savedPath != null) {
                _state.update { it.copy(imagePath = savedPath) }
            }
        }
    }

    fun onRemoveImage() {
        _state.value.imagePath?.let { oldPath ->
            mediaUseCases.deleteAudio(oldPath)
        }
        _state.update { it.copy(imagePath = null) }
    }

    fun startRecordingAudio() {
        currentAudioPath = mediaUseCases.startAudio()
    }

    fun stopAndSaveAudio(durationSeconds: Long) {
        mediaUseCases.stopAudio()
        currentAudioPath?.let { path ->
            val newVoiceNote = VoiceNote(id = 0, audioPath = path, duration = durationSeconds)
            _state.update { it.copy(voiceNotes = it.voiceNotes + newVoiceNote) }
        }
        currentAudioPath = null
    }

    fun cancelRecordingAudio() {
        mediaUseCases.stopAudio()
        currentAudioPath?.let { path ->
            mediaUseCases.deleteAudio(path)
        }
        currentAudioPath = null
    }

    // Audio Playback Actions
    fun onPlayPauseAudio(path: String) {
        if (_state.value.currentlyPlayingPath == path) {
            if (_state.value.isPlaying) {
                mediaUseCases.pauseAudio()
            } else {
                mediaUseCases.resumeAudio()
            }
        } else {
            _state.update { it.copy(currentlyPlayingPath = path) }
            mediaUseCases.playAudio(path)
        }
    }

    fun onSeekAudio(position: Float) {
        val seekMs = (position * _state.value.totalPlaybackDuration).toLong()
        mediaUseCases.seekAudio(seekMs)
    }

    fun resetState() {
        _state.value = NoteEditorState()
    }

    override fun onCleared() {
        super.onCleared()
        mediaUseCases.stopAudio() // Stop playback when ViewModel is destroyed
    }
}
