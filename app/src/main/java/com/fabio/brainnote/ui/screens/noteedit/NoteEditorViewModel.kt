package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabio.brainnote.domain.helper.formatToDateTime
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.model.VoiceNote
import com.fabio.brainnote.domain.usecase.CategoryUseCases
import com.fabio.brainnote.domain.usecase.MediaUseCases
import com.fabio.brainnote.domain.usecase.NoteUseCases
import com.fabio.brainnote.domain.usecase.link.SyncClusterCategoryUseCase
import com.fabio.brainnote.domain.usecase.link.SyncClusterPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
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
    private val syncClusterCategory: SyncClusterCategoryUseCase,
    private val syncClusterPin: SyncClusterPinUseCase,
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

    private var pendingCategoryTarget: Category? = null
    private var pendingPinTarget: Boolean? = null

    init {
        observeCategories()
        observePlaybackProgress()

        val noteId = savedStateHandle.get<Long>("noteId")
        val rootNoteId = savedStateHandle.get<Long>("rootNoteId")?.takeIf { it != -1L }

        if (noteId != null && noteId != -1L) {
            loadExistingNote(noteId)
            observeNoteHistory(noteId)
        }

        if (rootNoteId != null) {
            loadClusterContext(rootNoteId)
        }
    }

    private fun loadClusterContext(rootNoteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val rootNote = noteUseCases.getNoteById(rootNoteId)
            _state.update {
                it.copy(
                    clusterRootId = rootNoteId,
                    clusterRootTitle = rootNote?.title ?: ""
                )
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUseCases.getAll().collect { categories ->
                _state.update { it.copy(availableCategories = categories.toImmutableList()) }
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
                _state.update { it.copy(noteHistory = history.toImmutableList()) }
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

                val checkList = note.checklist.sortedBy { it.position }

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
                        checklists = checkList.toImmutableList(),
                        voiceNotes = note.voiceNotes.toImmutableList(),
                        reminders = note.reminders.toImmutableList(),
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
        val nextPinState = !_state.value.isPinned
        val isInCluster = _state.value.clusterRootId != null

        if (isInCluster) {
            pendingPinTarget = nextPinState
            showDialog(NoteEditorDialogState.ClusterPinChange(willPin = nextPinState))
        } else {
            _state.update { it.copy(isPinned = nextPinState) }
            checkChanges()
        }
    }

    fun onConfirmClusterPinChange() {
        val targetPinState = pendingPinTarget ?: return
        _state.update {
            it.copy(
                isPinned = targetPinState,
                activeDialog = null
            )
        }
        checkChanges()
    }

    fun onAddChecklistItem() {
        _state.update {  currentState ->
            val newItem = ChecklistItem(
                id = System.nanoTime(),
                text = "",
                isChecked = false,
                position = currentState.checklists.size
            )
            val updatedCheckList = currentState.checklists.toPersistentList().add(newItem)
            currentState.copy(checklists = updatedCheckList)
        }
        checkChanges()
    }

    fun onUpdateChecklistItem(index: Int, newText: String, isChecked: Boolean) {
        _state.update { currentState ->
            val updatedCheckList = currentState.checklists.toMutableList()
            if (index in updatedCheckList.indices) {
                updatedCheckList[index] = updatedCheckList[index].copy(text = newText, isChecked = isChecked)
                currentState.copy(checklists = updatedCheckList.toImmutableList())
            } else currentState
        }
        checkChanges()
    }

    fun onAddReminder(reminder: Reminder) {
        _state.update { currentState ->
            val updatedReminderList = currentState.reminders.toPersistentList().add(reminder)
            currentState.copy(reminders = updatedReminderList, activeDialog = null)
        }
        checkChanges()
    }

    fun showDialog(dialogState: NoteEditorDialogState) {
        _state.update { it.copy(activeDialog = dialogState) }
    }

    fun dismissDialog() {
        _state.update { it.copy(activeDialog = null) }
    }

    fun handleBackPress(onNavigateBack: () -> Unit) {
        if (_state.value.isEdited) {
            showDialog(NoteEditorDialogState.DiscardEditsConfirmation)
        } else {
            onNavigateBack()
        }
    }

    fun handleSaveRequest() {
        val s = _state.value
        val isNoteEmpty = s.title.isBlank() &&
                s.content.isBlank() &&
                s.checklists.isEmpty() &&
                s.voiceNotes.isEmpty() &&
                s.imagePath == null
        if (isNoteEmpty && s.noteId == null) {
            showDialog(NoteEditorDialogState.EmptyNoteWarning)
        } else {
            showDialog(NoteEditorDialogState.SaveConfirmation)
        }
    }

    fun confirmRemoveChecklistItem(index: Int) {
        _state.update { currentState ->
            val updatedCheckList = currentState.checklists.toMutableList()
            if (index in updatedCheckList.indices) {
                updatedCheckList.removeAt(index)
                val fixedCheckList = updatedCheckList.mapIndexed { i, item -> item.copy(position = i) }
                currentState.copy(checklists = fixedCheckList.toImmutableList(), activeDialog = null)
            } else currentState.copy(activeDialog = null)
        }
        checkChanges()
    }

    fun confirmRemoveReminder(index: Int) {
        _state.update { currentState ->
            if (index in currentState.reminders.indices) {
                val updatedReminderList = currentState.reminders.toPersistentList().removeAt(index)
                currentState.copy(reminders = updatedReminderList, activeDialog = null)
            } else currentState.copy(activeDialog = null)
        }
        checkChanges()
    }

    fun confirmRemoveVoiceNote(index: Int) {
        _state.update { currentState ->
            if (index in currentState.voiceNotes.indices) {
                val removedVoice = currentState.voiceNotes[index]
                viewModelScope.launch(Dispatchers.IO) {
                    mediaUseCases.deleteAudio(removedVoice.audioPath)
                    currentSessionVoices.remove(removedVoice.audioPath)
                }

                val updatedVoiceNoteList = currentState.voiceNotes.toPersistentList().removeAt(index)
                currentState.copy(
                    voiceNotes = updatedVoiceNoteList,
                    activeDialog = null
                )
            } else currentState.copy(activeDialog = null)
        }
        checkChanges()
    }


    fun onCategorySelected(categoryId: Long) {
        val newCategory = _state.value.availableCategories.find { it.id == categoryId }
        val oldCategory = _state.value.selectedCategory

        val isInCluster = _state.value.clusterRootId != null
        val categoryActuallyChanged = newCategory?.id != oldCategory?.id

        if (isInCluster && categoryActuallyChanged && newCategory != null) {
            val fromName = oldCategory?.name ?: "current category"
            val toName = newCategory.name
            pendingCategoryTarget = newCategory
            showDialog(NoteEditorDialogState.ClusterCategoryChange(fromName, toName))
        } else {
            _state.update { it.copy(selectedCategory = newCategory) }
            checkChanges()
        }
    }

    fun onConfirmClusterCategoryChange() {
        val pending = pendingCategoryTarget ?: return
        _state.update {
            it.copy(
                selectedCategory = pending,
                activeDialog = null
            )
        }
        pendingCategoryTarget = null
        checkChanges()
    }

    fun onDismissClusterCategoryDialog() {
        pendingCategoryTarget = null
        dismissDialog()
    }


    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value
            if (currentState.title.isBlank() &&
                currentState.content.isBlank() &&
                currentState.checklists.isEmpty() &&
                currentState.voiceNotes.isEmpty() &&
                currentState.imagePath == null
            ) return@launch

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
            if (currentState.checklists != originalChecklists &&
                !currentState.checklists.all { it in originalChecklists }
            ) modified.add("Checklist")

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

            val rootId = currentState.clusterRootId

            val categoryChanged = currentState.selectedCategory?.id != originalCategoryId
            if (categoryChanged && rootId != null && currentState.selectedCategory != null) {
                syncClusterCategory(rootId, currentState.selectedCategory)
            }

            val pinChanged = currentState.isPinned != originalPinned
            if (pinChanged && rootId != null) {
                syncClusterPin(rootId, currentState.isPinned)
            }

            currentSessionImages.clear()
            currentSessionVoices.clear()

            originalTitle = currentState.title
            originalContent = currentState.content
            originalCategoryId = currentState.selectedCategory?.id
            originalPinned = currentState.isPinned
            originalImagePath = currentState.imagePath
            originalChecklists = currentState.checklists
            originalVoiceNotes = currentState.voiceNotes
            originalReminders = currentState.reminders

            _state.update { it.copy(isSaved = true, isEdited = false, activeDialog = null) }
        }
    }


    fun deleteNote() {
        _state.value.noteId?.let { id ->
            viewModelScope.launch {
                noteUseCases.deleteNote(id)
                _state.update { it.copy(isSaved = true, isEdited = false, activeDialog = null) }
            }
        }
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
            val updatedVoiceNoteList = _state.value.voiceNotes.toPersistentList().add(newVoiceNote)
            _state.update {
                it.copy(
                    voiceNotes = updatedVoiceNoteList,
                    activeDialog = null
                )
            }
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
            currentSessionImages.forEach { path -> mediaUseCases.deleteImage(path) }
            currentSessionVoices.forEach { path -> mediaUseCases.deleteAudio(path) }
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