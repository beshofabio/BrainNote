package com.fabio.brainnote.ui.screens.noteedit.components


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.R
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.ui.components.ReminderDialog
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorDialogState
import com.fabio.brainnote.ui.screens.noteedit.NoteEditorState


@Composable
fun NoteEditorDialogHandler(
    state: NoteEditorState,
    onDismissClusterCategoryDialog: () -> Unit,
    colorScheme: ColorScheme,
    onConfirmClusterCategoryChange: () -> Unit,
    onDismissDialog: () -> Unit,
    onSaveNote: () -> Unit,
    onDeleteNote: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmRemoveChecklist: (Int) -> Unit,
    onConfirmRemoveVoice: (Int) -> Unit,
    onConfirmRemoveReminder: (Int) -> Unit,
    onAddReminder: (Reminder) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit,
    onCancelRecording: () -> Unit,
    onConfirmClusterPinChange: () -> Unit,
    onConfirmDiscard: () -> Unit
) {
    when (val dialog = state.activeDialog) {
        is NoteEditorDialogState.ClusterCategoryChange -> {
            AlertDialog(
                onDismissRequest = onDismissClusterCategoryDialog,
                containerColor = colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Change category for all notes?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "This note is part of a cluster. Changing the category from \"${dialog.fromName}\" to \"${dialog.toName}\" will apply to all linked notes in this cluster.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirmClusterCategoryChange) {
                        Text(text = "Yes, change all", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissClusterCategoryDialog) { Text(text = "Cancel") }
                }
            )
        }

        NoteEditorDialogState.EmptyNoteWarning -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.empty_note_title)) },
                text = { Text(stringResource(R.string.empty_note_msg)) },
                confirmButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text(stringResource(R.string.ok_btn), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        NoteEditorDialogState.SaveConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.save_changes_title)) },
                text = { Text(stringResource(R.string.save_changes_msg)) },
                confirmButton = {
                    TextButton(onClick = onSaveNote) {
                        Text(stringResource(R.string.save_btn), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel_btn)) }
                }
            )
        }

        NoteEditorDialogState.DeleteConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = {
                    Text(
                        stringResource(R.string.delete_note_title),
                        color = colorScheme.error
                    )
                },
                text = { Text(stringResource(R.string.delete_note_msg)) },
                confirmButton = {
                    TextButton(onClick = onDeleteNote) {
                        Text(
                            stringResource(R.string.delete_btn),
                            color = colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel_btn)) }
                }
            )
        }

        NoteEditorDialogState.DiscardEditsConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.discard_edits_title)) },
                text = { Text(stringResource(R.string.discard_edits_msg)) },
                confirmButton = {
                    TextButton(onClick = {
                        onDismissDialog()
                        onConfirmDiscard()
                    }) {
                        Text(
                            stringResource(R.string.discard_btn),
                            color = colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.keep_editing_btn)) }
                }
            )
        }

        is NoteEditorDialogState.RemoveChecklistItemConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.remove_checklist_title)) },
                text = { Text(stringResource(R.string.remove_checklist_msg)) },
                confirmButton = {
                    TextButton(onClick = { onConfirmRemoveChecklist(dialog.index) }) {
                        Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel_btn)) }
                }
            )
        }

        is NoteEditorDialogState.RemoveVoiceNoteConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.remove_voice_title)) },
                text = { Text(stringResource(R.string.remove_voice_msg)) },
                confirmButton = {
                    TextButton(onClick = { onConfirmRemoveVoice(dialog.index) }) {
                        Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel_btn)) }
                }
            )
        }

        is NoteEditorDialogState.RemoveReminderConfirmation -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text(stringResource(R.string.remove_reminder_title)) },
                text = { Text(stringResource(R.string.remove_reminder_msg)) },
                confirmButton = {
                    TextButton(onClick = { onConfirmRemoveReminder(dialog.index) }) {
                        Text(stringResource(R.string.remove_btn), color = colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel_btn)) }
                }
            )
        }

        NoteEditorDialogState.HistorySheet -> {
            NoteHistoryBottomSheet(
                historyList = state.noteHistory,
                onDismiss = onDismissDialog
            )
        }

        NoteEditorDialogState.Reminder -> {
            ReminderDialog(
                onDismiss = onDismissDialog,
                onSave = { triggerTime, repeatType ->
                    onAddReminder(Reminder(triggerTime = triggerTime, repeatType = repeatType))
                }
            )
        }

        NoteEditorDialogState.RecordVoice -> {
            RecordVoiceDialog(
                onStartRecording = onStartRecording,
                onStopRecording = { duration -> onStopRecording(duration) },
                onCancelRecording = onCancelRecording
            )
        }

        is NoteEditorDialogState.ClusterPinChange -> {
            val actionTitle = if (dialog.willPin) "Pin Entire Cluster?" else "Unpin Entire Cluster?"
            val actionMessage = if (dialog.willPin) {
                "This note is part of a cluster. Pinning it will pin all other notes in this cluster to keep them grouped."
            } else {
                "Unpinning this note will unpin all other notes connected within this cluster."
            }
            AlertDialog(
                onDismissRequest = onDismissDialog,
                containerColor = colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                title = { Text(actionTitle, fontWeight = FontWeight.Bold) },
                text = { Text(actionMessage) },
                confirmButton = {
                    TextButton(onClick = onConfirmClusterPinChange) {
                        Text("Sync Pin", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialog) { Text("Cancel") }
                }
            )
        }

        null -> {
        }
    }
}