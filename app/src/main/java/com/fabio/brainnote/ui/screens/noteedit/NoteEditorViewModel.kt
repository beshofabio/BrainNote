package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabio.brainnote.domain.helper.formatToDateTime
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

    private var originalTitle: String = ""
    private var originalContent: String = ""
    private var originalCategoryId: Long? = null
    private var originalPinned: Boolean = false
    private var originalImagePath: String? = null
    private var originalChecklists: List<ChecklistItem> = emptyList()
    private var originalVoiceNotes: List<VoiceNote> = emptyList()
    private var originalReminders: List<Reminder> = emptyList()


    private val currentSessionImages = mutableSetOf<String>()
    private val currentSessionVoices = mutableSetOf<String>()

    init {
        observeCategories()
        observePlaybackProgress()

        val noteId = savedStateHandle.get<Long>("noteId")
        if (noteId != null && noteId != -1L) {
            loadExistingNote(noteId)
            observeNoteHistory(noteId)
        }
    }

    private fun observeCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUseCases.getAll().collect { categories ->
                _state.update { it.copy(availableCategories = categories) }
                if (_state.value.noteId == null && _state.value.selectedCategory == null && categories.isNotEmpty()) {
                    val defaultCat = categories.first()
                    _state.update { it.copy(selectedCategory = defaultCat) }
                    originalCategoryId = defaultCat.id
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

    private fun observeNoteHistory(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteUseCases.getNoteHistory(noteId).collect { history ->
                _state.update { it.copy(noteHistory = history) }
            }
        }
    }

    private fun loadExistingNote(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val note = noteUseCases.getNoteById(noteId)
            if (note != null) {
                originalCreatedAt = note.createdAt
                originalTitle = note.title
                originalContent = note.content
                originalCategoryId = note.category?.id
                originalPinned = note.isPinned
                originalImagePath = note.imagePath
                originalChecklists = note.checklist
                originalVoiceNotes = note.voiceNotes
                originalReminders = note.reminders

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
                        checklists = note.checklist.sortedBy { item -> item.position },
                        voiceNotes = note.voiceNotes,
                        reminders = note.reminders,
                        isLoading = false,
                        isEdited = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkChanges() {
        val s = _state.value
        val hasChanges = s.title != originalTitle ||
                s.content != originalContent ||
                s.selectedCategory?.id != originalCategoryId ||
                s.isPinned != originalPinned ||
                s.imagePath != originalImagePath ||
                s.checklists != originalChecklists ||
                s.voiceNotes != originalVoiceNotes ||
                s.reminders != originalReminders
        
        _state.update { it.copy(isEdited = hasChanges) }
    }

    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
        checkChanges()
    }

    fun onContentChange(newContent: String) {
        _state.update { it.copy(content = newContent) }
        checkChanges()
    }

    fun onTogglePin() {
        _state.update { it.copy(isPinned = !it.isPinned) }
        checkChanges()
    }

    fun onAddChecklistItem() {
        val newItem = ChecklistItem(id = System.nanoTime(), text = "", isChecked = false, position = _state.value.checklists.size)
        _state.update { it.copy(checklists = it.checklists + newItem) }
        checkChanges()
    }

    fun onUpdateChecklistItem(index: Int, newText: String, isChecked: Boolean) {
        _state.update { currentState ->
            val updatedList = currentState.checklists.toMutableList()
            if (index in updatedList.indices) {
                updatedList[index] = updatedList[index].copy(text = newText, isChecked = isChecked)
                currentState.copy(checklists = updatedList)
            } else currentState
        }
        checkChanges()
    }

    fun onRemoveChecklistItem(index: Int) {
        _state.update { currentState ->
            val updatedList = currentState.checklists.toMutableList()
            if (index in updatedList.indices) {
                updatedList.removeAt(index)
                val fixed = updatedList.mapIndexed { i, item -> item.copy(position = i) }
                currentState.copy(checklists = fixed)
            } else currentState
        }
        checkChanges()
    }

    fun onAddReminder(reminder: Reminder) {
        _state.update { it.copy(reminders = it.reminders + reminder) }
        checkChanges()
    }

    fun onRemoveReminder(index: Int) {
        _state.update { currentState ->
            val updatedList = currentState.reminders.toMutableList()
            if (index in updatedList.indices) {
                updatedList.removeAt(index)
                currentState.copy(reminders = updatedList)
            } else currentState
        }
        checkChanges()
    }

    fun onRemoveVoiceNote(index: Int) {
        _state.update { currentState ->
            val updatedList = currentState.voiceNotes.toMutableList()
            if (index in updatedList.indices) {
                val removedVoice = updatedList.removeAt(index)

                if (currentSessionVoices.contains(removedVoice.audioPath)) {
                    viewModelScope.launch(Dispatchers.IO) {
                        mediaUseCases.deleteAudio(removedVoice.audioPath)
                        currentSessionVoices.remove(removedVoice.audioPath)
                    }
                }

                currentState.copy(voiceNotes = updatedList)
            } else currentState
        }
        checkChanges()
    }

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value
            if (currentState.title.isBlank() && currentState.content.isBlank() && currentState.checklists.isEmpty() && currentState.voiceNotes.isEmpty() && currentState.imagePath == null) {
                return@launch
            }

            val modified = mutableListOf<String>()
            val removed = mutableListOf<String>()

            if (currentState.title != originalTitle) modified.add("Title")
            if (currentState.content != originalContent) modified.add("Description")
            if (currentState.selectedCategory?.id != originalCategoryId) modified.add("Category")
            if (currentState.isPinned != originalPinned) modified.add("Pin Status")
            
            if (currentState.imagePath != originalImagePath) {
                if (currentState.imagePath == null) removed.add("Image") else modified.add("Image")
            }

            val currentCheckIds = currentState.checklists.map { it.id }.toSet()
            if (originalChecklists.any { it.id !in currentCheckIds }) removed.add("Checklist item(s)")
            if (currentState.checklists != originalChecklists && !currentState.checklists.all { it in originalChecklists }) modified.add("Checklist")

            val currentVoicePaths = currentState.voiceNotes.map { it.audioPath }.toSet()
            if (originalVoiceNotes.any { it.audioPath !in currentVoicePaths }) removed.add("Voice recording(s)")
            if (currentState.voiceNotes.size > originalVoiceNotes.size) modified.add("Voice recording(s)")

            val currentRemIds = currentState.reminders.map { it.id }.toSet()
            originalReminders.filter { it.id !in currentRemIds }.forEach {
                removed.add("Reminder at ${it.triggerTime.formatToDateTime()}")
            }
            currentState.reminders.filter { it !in originalReminders }.forEach {
                modified.add("Reminder at ${it.triggerTime.formatToDateTime()}")
            }

            val summaryParts = mutableListOf<String>()
            if (modified.isNotEmpty()) summaryParts.add("Modified: ${modified.distinct().joinToString(", ")}")
            if (removed.isNotEmpty()) summaryParts.add("Removed: ${removed.distinct().joinToString(", ")}")
            val finalSummary = if (summaryParts.isEmpty()) "Quick Save" else summaryParts.joinToString(" | ")

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

            noteUseCases.saveFullNote(noteToSave, finalSummary)

            currentSessionImages.clear()
            currentSessionVoices.clear()

            originalTitle = currentState.title; originalContent = currentState.content
            originalCategoryId = currentState.selectedCategory?.id; originalPinned = currentState.isPinned
            originalImagePath = currentState.imagePath; originalChecklists = currentState.checklists
            originalVoiceNotes = currentState.voiceNotes; originalReminders = currentState.reminders

            _state.update { it.copy(isSaved = true, isEdited = false) }
        }
    }

    fun deleteNote() {
        _state.value.noteId?.let { id ->
            viewModelScope.launch {
                noteUseCases.deleteNote(id)
                _state.update { it.copy(isSaved = true, isEdited = false) }
            }
        }
    }

    fun onCategorySelected(categoryId: Long) {
        val category = _state.value.availableCategories.find { it.id == categoryId }
        _state.update { it.copy(selectedCategory = category) }
        checkChanges()
    }

    fun onImageConfirmed(uri: Uri) {
        viewModelScope.launch {
            val savedPath = mediaUseCases.saveImage(uri.toString())
            if (savedPath != null) {
                currentSessionImages.add(savedPath)
                _state.update { it.copy(imagePath = savedPath) }
                checkChanges()
            }
        }
    }

    fun onRemoveImage() {
        val removedPath = _state.value.imagePath
        _state.update { it.copy(imagePath = null) }
        checkChanges()

        if (removedPath != null && currentSessionImages.contains(removedPath)) {
            viewModelScope.launch(Dispatchers.IO) {
                mediaUseCases.deleteImage(removedPath)
                currentSessionImages.remove(removedPath)
            }
        }
    }

    fun startRecordingAudio() {
        currentAudioPath = mediaUseCases.startAudio()
    }

    fun stopAndSaveAudio(durationSeconds: Long) {
        mediaUseCases.stopAudio()
        currentAudioPath?.let { path ->
            currentSessionVoices.add(path)

            val newVoiceNote = VoiceNote(id = 0, audioPath = path, duration = durationSeconds)
            _state.update { it.copy(voiceNotes = it.voiceNotes + newVoiceNote) }
            checkChanges()
        }
        currentAudioPath = null
    }

    fun cancelRecordingAudio() {
        mediaUseCases.stopAudio()
        currentAudioPath = null
    }

    fun onPlayPauseAudio(path: String) {
        if (_state.value.currentlyPlayingPath == path) {
            if (_state.value.isPlaying) mediaUseCases.pauseAudio() else mediaUseCases.resumeAudio()
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
        mediaUseCases.stopAudio()

        val currentlyLoadedCategories = _state.value.availableCategories
        val defaultCat = currentlyLoadedCategories.first()

        originalTitle = ""
        originalContent = ""
        originalCategoryId = null
        originalPinned = false
        originalImagePath = null
        originalChecklists = emptyList()
        originalVoiceNotes = emptyList()
        originalReminders = emptyList()

        currentSessionImages.clear()
        currentSessionVoices.clear()

        _state.value = NoteEditorState().copy(
            availableCategories = currentlyLoadedCategories,
            selectedCategory = defaultCat
        )
    }

    fun discardEdits() {
        viewModelScope.launch(Dispatchers.IO) {
            currentSessionImages.forEach { path ->
                mediaUseCases.deleteImage(path)
            }
            currentSessionVoices.forEach { path ->
                mediaUseCases.deleteAudio(path)
            }

            currentSessionImages.clear()
            currentSessionVoices.clear()
            resetState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaUseCases.stopAudio()
    }
}
