package com.fabio.brainnote.ui.screens.noteedit

sealed class NoteEditorDialogState {
    object SaveConfirmation : NoteEditorDialogState()
    object DeleteConfirmation : NoteEditorDialogState()
    object DiscardEditsConfirmation : NoteEditorDialogState()
    object EmptyNoteWarning : NoteEditorDialogState()

    data class RemoveChecklistItemConfirmation(val index: Int) : NoteEditorDialogState()
    data class RemoveVoiceNoteConfirmation(val index: Int) : NoteEditorDialogState()
    data class RemoveReminderConfirmation(val index: Int) : NoteEditorDialogState()

    data class ClusterCategoryChange(val fromName: String, val toName: String) : NoteEditorDialogState()
    data class ClusterPinChange(val willPin: Boolean) : NoteEditorDialogState()

    object HistorySheet : NoteEditorDialogState()
    object Reminder : NoteEditorDialogState()
    object RecordVoice : NoteEditorDialogState()
}