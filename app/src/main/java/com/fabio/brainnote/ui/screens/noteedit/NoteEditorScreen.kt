package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.R
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.model.VoiceNote
import com.fabio.brainnote.ui.components.ReminderDialog
import com.fabio.brainnote.ui.components.VoiceNotePlayer
import com.fabio.brainnote.ui.components.CategoriesLazyRow
import com.fabio.brainnote.ui.screens.noteedit.components.EditorBottomBar
import com.fabio.brainnote.ui.screens.noteedit.components.EditorChecklistItem
import com.fabio.brainnote.ui.screens.noteedit.components.EditorImageHeader
import com.fabio.brainnote.ui.screens.noteedit.components.EditorTextField
import com.fabio.brainnote.ui.screens.noteedit.components.EditorTopBar
import com.fabio.brainnote.ui.screens.noteedit.components.ImagePreviewDialog
import com.fabio.brainnote.ui.screens.noteedit.components.RecordVoiceDialog
import com.fabio.brainnote.ui.theme.BrainNoteTheme

@Composable
fun NoteEditorScreen(
    state: NoteEditorState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onTogglePin: () -> Unit,
    onCategorySelected: (Long) -> Unit,

    onAddChecklistItem: () -> Unit,
    onUpdateChecklistItem: (Int, String, Boolean) -> Unit,
    onRemoveChecklistItem: (Int) -> Unit,

    onImageConfirmed: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onAddReminder: (Reminder) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit,
    onCancelRecording: () -> Unit,

    onPlayPauseAudio: (String) -> Unit,
    onSeekAudio: (Float) -> Unit,

    onSaveNote: () -> Unit,
    onClearError: () -> Unit
){

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    var showReminderDialog by remember { mutableStateOf(false) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingImageUri = uri
        }
    }

    if (pendingImageUri != null) {
        ImagePreviewDialog(
            uri = pendingImageUri!!,
            onConfirm = {
                onImageConfirmed(pendingImageUri!!)
                pendingImageUri = null
            },
            onDismiss = { pendingImageUri = null }
        )
    }

    if (showReminderDialog) {
        ReminderDialog(
            onDismiss = { showReminderDialog = false },
            onSave = { triggerTime, repeatType ->
                onAddReminder(Reminder(triggerTime = triggerTime, repeatType = repeatType))
                showReminderDialog = false
            }
        )
    }

    if (showRecordDialog) {
        RecordVoiceDialog(
            onStartRecording = onStartRecording,
            onStopRecording = { duration ->
                onStopRecording(duration)
                showRecordDialog = false
            },
            onCancelRecording = {
                onCancelRecording()
                showRecordDialog = false
            }
        )
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBackClick()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            EditorTopBar(
                isPinned = state.isPinned,
                onBackClick = onBackClick,
                onTogglePin = onTogglePin,
                onSaveClick = onSaveNote,
                onOptionsClick = { /* TODO Options */ }
            )
        },
        bottomBar = {
            EditorBottomBar(
                onAddChecklist = onAddChecklistItem,
                onAddImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRecordAudio = { showRecordDialog = true },
                onAddReminder = { showReminderDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            if (state.imagePath != null) {
                EditorImageHeader(imagePath = state.imagePath, onRemove = onRemoveImage)
            }

            if (state.availableCategories.isNotEmpty()) {
                CategoriesLazyRow(
                    categories = state.availableCategories,
                    selectedCategoryId = state.selectedCategory?.id ?: -1L,
                    onCategoryClick = onCategorySelected
                )
            }

            EditorTextField(
                value = state.title,
                onValueChange = onTitleChange,
                placeholder = "Title",
                isTitle = true
            )

            EditorTextField(
                value = state.content,
                onValueChange = onContentChange,
                placeholder = "Content",
                isTitle = false
            )

            if (state.checklists.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.checklists.forEachIndexed { index, item ->
                        EditorChecklistItem(
                            item = item,
                            onCheckedChange = { isChecked ->
                                onUpdateChecklistItem(index, item.text, isChecked)
                            },
                            onTextChange = { newText ->
                                onUpdateChecklistItem(index, newText, item.isChecked)
                            },
                            onRemove = {
                                onRemoveChecklistItem(index)
                            }
                        )
                    }
                }
            }

            if (state.voiceNotes.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.voiceNotes.forEach { voice ->
                        val isThisPlaying = state.currentlyPlayingPath == voice.audioPath
                        VoiceNotePlayer(
                            durationSeconds = voice.duration,
                            isPlaying = isThisPlaying && state.isPlaying,
                            currentPositionMs = if (isThisPlaying) state.currentPlaybackPosition else 0L,
                            onPlayPauseClick = { onPlayPauseAudio(voice.audioPath) },
                            onSeek = { if (isThisPlaying) onSeekAudio(it) },
                            switchColor = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteEditorScreenPreview() {
    val dummyState = NoteEditorState(
        title = "Note App Idea",
        content = "We need to ensure the Room database is fully implemented before we write the UseCases. Also, don't forget the dark mode theme colors",
        selectedCategory = Category(id = 1, name = "Development", color = 0xFF4285F4, icon = R.drawable.work),
        imagePath = "fake_path.jpg",
        checklists = listOf(
            ChecklistItem(id = 1, text = "Create Room Database", isChecked = true, position = 1),
            ChecklistItem(id = 2, text = "Create NoteEditorScreen", isChecked = false, position = 2)
        ),
        voiceNotes = listOf(VoiceNote(id = 1, audioPath = "/storage/emulated/0/BrainNote/audio_1.m4a", duration = 40))
    )

    BrainNoteTheme {
        NoteEditorScreen(
            state = dummyState,
            onTitleChange = {},
            onContentChange = {},
            onBackClick = {},
            onTogglePin = {},
            onCategorySelected = {},
            onAddChecklistItem = {},
            onUpdateChecklistItem = {_, _, _ ->},
            onRemoveChecklistItem = {},
            onImageConfirmed = {},
            onRemoveImage = {},
            onAddReminder = {},
            onStartRecording = {},
            onStopRecording = {},
            onCancelRecording = {},
            onPlayPauseAudio = {},
            onSeekAudio = {},
            onSaveNote = {},
            onClearError = {}
        )
    }
}
