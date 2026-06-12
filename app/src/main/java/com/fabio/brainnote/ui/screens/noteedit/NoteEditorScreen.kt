package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabio.brainnote.R
import com.fabio.brainnote.domain.helper.formatToDateTime
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
import com.fabio.brainnote.ui.screens.noteedit.components.NoteHistoryBottomSheet
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
    onRemoveReminder: (Int) -> Unit,

    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit,
    onCancelRecording: () -> Unit,
    onRemoveVoiceNote: (Int) -> Unit,

    onPlayPauseAudio: (String) -> Unit,
    onSeekAudio: (Float) -> Unit,

    onSaveNote: () -> Unit,
    onDeleteNote: () -> Unit
){
    val colorScheme = MaterialTheme.colorScheme

    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showEmptyDialog by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }

    var pendingRemoveChecklistIndex by remember { mutableStateOf<Int?>(null) }
    var pendingRemoveVoiceIndex by remember { mutableStateOf<Int?>(null) }
    var pendingRemoveReminderIndex by remember { mutableStateOf<Int?>(null) }
    
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

    val handleBack = {
        if (state.isEdited) {
            showDiscardDialog = true
        } else {
            onBackClick()
        }
    }

    val handleSaveRequest = {
        val isNoteEmpty = state.title.isBlank() && 
                         state.content.isBlank() && 
                         state.checklists.isEmpty() && 
                         state.voiceNotes.isEmpty() && 
                         state.imagePath == null
        
        if (isNoteEmpty && state.noteId == null) {
            showEmptyDialog = true
        } else {
            showSaveDialog = true
        }
    }

    BackHandler(enabled = true) {
        handleBack()
    }

    if (showHistorySheet) {
        NoteHistoryBottomSheet(
            historyList = state.noteHistory,
            onDismiss = { showHistorySheet = false }
        )
    }

    if (showEmptyDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyDialog = false },
            title = { Text(stringResource(R.string.empty_note_title)) },
            text = { Text(stringResource(R.string.empty_note_msg)) },
            confirmButton = {
                TextButton(onClick = { showEmptyDialog = false }) {
                    Text(stringResource(R.string.ok_btn), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(stringResource(R.string.save_changes_title)) },
            text = { Text(stringResource(R.string.save_changes_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    onSaveNote()
                    showSaveDialog = false 
                }) {
                    Text(stringResource(R.string.save_btn), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_note_title), color = colorScheme.error) },
            text = { Text(stringResource(R.string.delete_note_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    onDeleteNote()
                    showDeleteDialog = false 
                }) {
                    Text(stringResource(R.string.delete_btn), color = colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_edits_title)) },
            text = { Text(stringResource(R.string.discard_edits_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    showDiscardDialog = false
                    onBackClick()
                }) {
                    Text(stringResource(R.string.discard_btn), color = colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.keep_editing_btn))
                }
            }
        )
    }

    if (pendingRemoveChecklistIndex != null) {
        AlertDialog(
            onDismissRequest = { pendingRemoveChecklistIndex = null },
            title = { Text(stringResource(R.string.remove_checklist_title)) },
            text = { Text(stringResource(R.string.remove_checklist_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    onRemoveChecklistItem(pendingRemoveChecklistIndex!!)
                    pendingRemoveChecklistIndex = null 
                }) {
                    Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoveChecklistIndex = null }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    if (pendingRemoveVoiceIndex != null) {
        AlertDialog(
            onDismissRequest = { pendingRemoveVoiceIndex = null },
            title = { Text(stringResource(R.string.remove_voice_title)) },
            text = { Text(stringResource(R.string.remove_voice_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    onRemoveVoiceNote(pendingRemoveVoiceIndex!!)
                    pendingRemoveVoiceIndex = null 
                }) {
                    Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoveVoiceIndex = null }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
    }

    if (pendingRemoveReminderIndex != null) {
        AlertDialog(
            onDismissRequest = { pendingRemoveReminderIndex = null },
            title = { Text(stringResource(R.string.remove_reminder_title)) },
            text = { Text(stringResource(R.string.remove_reminder_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    onRemoveReminder(pendingRemoveReminderIndex!!)
                    pendingRemoveReminderIndex = null 
                }) {
                    Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoveReminderIndex = null }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        )
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
                isNewNote = state.noteId == null,
                onBackClick = handleBack,
                onTogglePin = onTogglePin,
                onSaveClick = handleSaveRequest,
                onDeleteClick = { showDeleteDialog = true },
                onHistoryClick = { showHistorySheet = true }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
        ) {
            item {
                if (state.imagePath != null) {
                    EditorImageHeader(imagePath = state.imagePath, onRemove = onRemoveImage)
                }
            }

            item {
                if (state.availableCategories.isNotEmpty()) {
                    CategoriesLazyRow(
                        categories = state.availableCategories,
                        selectedCategoryId = state.selectedCategory?.id ?: -1L,
                        onCategoryClick = onCategorySelected
                    )
                }
            }

            item {
                EditorTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    placeholder = "Title",
                    isTitle = true
                )
            }

            item {
                EditorTextField(
                    value = state.content,
                    onValueChange = onContentChange,
                    placeholder = "Content",
                    isTitle = false
                )
            }

            itemsIndexed(
                items = state.checklists,
                key = { _, item -> item.id } 
            ) { index, item ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem() 
                ) {
                    EditorChecklistItem(
                        item = item,
                        onCheckedChange = { isChecked ->
                            onUpdateChecklistItem(index, item.text, isChecked)
                        },
                        onTextChange = { newText ->
                            onUpdateChecklistItem(index, newText, item.isChecked)
                        },
                        onRemove = {
                            pendingRemoveChecklistIndex = index
                        }
                    )
                }
            }

            if (state.checklists.isNotEmpty()) {
                item(key = "add_more_checklist_button") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .animateItem() 
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAddChecklistItem() }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckBox,
                            contentDescription = null,
                            tint = colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.add_1_more),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            val upcomingReminders = state.reminders.filter { it.triggerTime > System.currentTimeMillis() }
            if (upcomingReminders.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = stringResource(R.string.upcoming_reminder),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        upcomingReminders.forEach { reminder ->
                            val originalIndex = state.reminders.indexOf(reminder)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = null
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = reminder.triggerTime.formatToDateTime(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { pendingRemoveReminderIndex = originalIndex },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove reminder",
                                            tint = colorScheme.onSurface.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (state.voiceNotes.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.voiceNotes.forEachIndexed { index, voice ->
                            val isThisPlaying = state.currentlyPlayingPath == voice.audioPath
                            VoiceNotePlayer(
                                durationSeconds = voice.duration,
                                isPlaying = isThisPlaying && state.isPlaying,
                                currentPositionMs = if (isThisPlaying) state.currentPlaybackPosition else 0L,
                                totalDurationMs = if (isThisPlaying) state.totalPlaybackDuration else voice.duration * 1000L,
                                onPlayPauseClick = { onPlayPauseAudio(voice.audioPath) },
                                onSeek = { if (isThisPlaying) onSeekAudio(it) },
                                onRemoveClick = { pendingRemoveVoiceIndex = index },
                                switchColor = true
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteEditorScreenPreview() {
    val dummyState = NoteEditorState(
        title = "Note App Idea",
        content = "We need to ensure the Room database is fully implemented before we write the UseCases.",
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
            onRemoveReminder = {},
            onStartRecording = {},
            onStopRecording = {},
            onCancelRecording = {},
            onRemoveVoiceNote = {},
            onPlayPauseAudio = {},
            onSeekAudio = {},
            onSaveNote = {},
            onDeleteNote = {}
        )
    }
}
