package com.fabio.brainnote.ui.screens.noteedit

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabio.brainnote.R
import com.fabio.brainnote.domain.helper.formatToDateTime
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.ui.components.CategoriesLazyRow
import com.fabio.brainnote.ui.components.VoiceNotePlayer
import com.fabio.brainnote.ui.screens.noteedit.components.EditorBottomBar
import com.fabio.brainnote.ui.screens.noteedit.components.EditorChecklistItem
import com.fabio.brainnote.ui.screens.noteedit.components.EditorImageHeader
import com.fabio.brainnote.ui.screens.noteedit.components.EditorTextField
import com.fabio.brainnote.ui.screens.noteedit.components.EditorTopBar
import com.fabio.brainnote.ui.screens.noteedit.components.ImagePreviewDialog
import com.fabio.brainnote.ui.screens.noteedit.components.NoteEditorDialogHandler

@OptIn(ExperimentalMaterial3Api::class)
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

    onShowDialog: (NoteEditorDialogState) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmRemoveChecklist: (Int) -> Unit,
    onConfirmRemoveVoice: (Int) -> Unit,
    onConfirmRemoveReminder: (Int) -> Unit,
    onRequestBackPress: () -> Unit,
    onRequestSave: () -> Unit,

    onImageConfirmed: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onAddReminder: (Reminder) -> Unit,

    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit,
    onCancelRecording: () -> Unit,

    onPlayPauseAudio: (String) -> Unit,
    onSeekAudio: (Float) -> Unit,

    onSaveNote: () -> Unit,
    onDeleteNote: () -> Unit,

    onConfirmClusterCategoryChange: () -> Unit,
    onDismissClusterCategoryDialog: () -> Unit,
    onConfirmClusterPinChange: () -> Unit,
    onConfirmDiscard: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) pendingImageUri = uri }

    BackHandler(enabled = true) { onRequestBackPress() }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBackClick()
    }


    NoteEditorDialogHandler(
        state = state,
        onDismissClusterCategoryDialog = onDismissClusterCategoryDialog,
        colorScheme = colorScheme,
        onConfirmClusterCategoryChange = onConfirmClusterCategoryChange,
        onDismissDialog = onDismissDialog,
        onSaveNote = onSaveNote,
        onDeleteNote = onDeleteNote,
        onConfirmRemoveChecklist = onConfirmRemoveChecklist,
        onConfirmRemoveVoice = onConfirmRemoveVoice,
        onConfirmRemoveReminder = onConfirmRemoveReminder,
        onAddReminder = onAddReminder,
        onStartRecording = onStartRecording,
        onStopRecording = onStopRecording,
        onCancelRecording = onCancelRecording,
        onConfirmClusterPinChange = onConfirmClusterPinChange,
        onConfirmDiscard = onConfirmDiscard
    )


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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            EditorTopBar(
                isPinned = state.isPinned,
                isNewNote = state.noteId == null,
                isInCluster = state.clusterRootId != null,
                onBackClick = onRequestBackPress,
                onTogglePin = onTogglePin,
                onSaveClick = onRequestSave,
                onDeleteClick = { onShowDialog(NoteEditorDialogState.DeleteConfirmation) },
                onHistoryClick = { onShowDialog(NoteEditorDialogState.HistorySheet) }
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
                onRecordAudio = { onShowDialog(NoteEditorDialogState.RecordVoice) },
                onAddReminder = { onShowDialog(NoteEditorDialogState.Reminder) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
        ) {
            if (state.clusterRootId != null && state.clusterRootTitle != null) {
                item(key = "cluster_banner") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schema,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Part of: ${state.clusterRootTitle}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

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
                        onRemove = { onShowDialog(NoteEditorDialogState.RemoveChecklistItemConfirmation(index)) }
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
                                color = colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                                        onClick = { onShowDialog(NoteEditorDialogState.RemoveReminderConfirmation(originalIndex)) },
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
                                onRemoveClick = { onShowDialog(NoteEditorDialogState.RemoveVoiceNoteConfirmation(index)) },
                                switchColor = true
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}