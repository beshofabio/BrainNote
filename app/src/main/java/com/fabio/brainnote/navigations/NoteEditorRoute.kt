package com.fabio.brainnote.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorScreen
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorViewModel

@Composable
fun NoteEditorRoute(
    onBackClick: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteEditorScreen(
        state = state,
        onBackClick = onBackClick,
        
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onTogglePin = viewModel::onTogglePin,
        onCategorySelected = viewModel::onCategorySelected,

        onAddChecklistItem = viewModel::onAddChecklistItem,
        onUpdateChecklistItem = viewModel::onUpdateChecklistItem,
        onRemoveChecklistItem = viewModel::onRemoveChecklistItem,

        onImageConfirmed = viewModel::onImageConfirmed,
        onRemoveImage = viewModel::onRemoveImage,
        onAddReminder = viewModel::onAddReminder,
        onRemoveReminder = viewModel::onRemoveReminder,

        onStartRecording = viewModel::startRecordingAudio,
        onStopRecording = viewModel::stopAndSaveAudio,
        onCancelRecording = viewModel::cancelRecordingAudio,
        onRemoveVoiceNote = viewModel::onRemoveVoiceNote,

        onPlayPauseAudio = viewModel::onPlayPauseAudio,
        onSeekAudio = viewModel::onSeekAudio,

        onSaveNote = viewModel::saveNote,
        onDeleteNote = viewModel::deleteNote
    )
}
