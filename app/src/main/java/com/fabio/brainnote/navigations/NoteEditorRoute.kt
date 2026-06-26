package com.fabio.brainnote.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorScreen
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorViewModel

@Composable
fun NoteEditorRoute(
    onBackClick: () -> Unit,
) {
    val viewModel : NoteEditorViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val onBackRequest = { viewModel.handleBackPress(onBackClick) }

    NoteEditorScreen(
        state = state,
        onBackClick = onBackRequest,

        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onTogglePin = viewModel::onTogglePin,
        onCategorySelected = viewModel::onCategorySelected,

        onAddChecklistItem = viewModel::onAddChecklistItem,
        onUpdateChecklistItem = viewModel::onUpdateChecklistItem,

        onShowDialog = viewModel::showDialog,
        onDismissDialog = viewModel::dismissDialog,
        onConfirmRemoveChecklist = viewModel::confirmRemoveChecklistItem,
        onConfirmRemoveVoice = viewModel::confirmRemoveVoiceNote,
        onConfirmRemoveReminder = viewModel::confirmRemoveReminder,
        
        onRequestBackPress = onBackRequest,
        onRequestSave = viewModel::handleSaveRequest,

        onImageConfirmed = viewModel::onImageConfirmed,
        onRemoveImage = viewModel::onRemoveImage,
        onAddReminder = viewModel::onAddReminder,

        onStartRecording = viewModel::startRecordingAudio,
        onStopRecording = viewModel::stopAndSaveAudio,
        onCancelRecording = viewModel::cancelRecordingAudio,

        onPlayPauseAudio = viewModel::onPlayPauseAudio,
        onSeekAudio = viewModel::onSeekAudio,

        onSaveNote = viewModel::saveNote,
        onDeleteNote = viewModel::deleteNote,

        onConfirmClusterCategoryChange = viewModel::onConfirmClusterCategoryChange,
        onDismissClusterCategoryDialog = viewModel::onDismissClusterCategoryDialog,
        onConfirmClusterPinChange = viewModel::onConfirmClusterPinChange,
        
        onConfirmDiscard = { viewModel.confirmDiscard(onBackClick) }
    )
}