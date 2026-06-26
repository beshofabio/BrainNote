package com.fabio.brainnote.ui.screens.noteedit

import androidx.compose.runtime.Immutable
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.model.VoiceNote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class NoteEditorState(
    val noteId: Long? = null,
    val title: String = "",
    val content: String = "",
    val imagePath: String? = null,
    val selectedCategory: Category? = null,
    val availableCategories: ImmutableList<Category> = persistentListOf(),
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val colorPriority: Int = 0xFFFFC34D.toInt(),
    val checklists: ImmutableList<ChecklistItem> = persistentListOf(),
    val voiceNotes: ImmutableList<VoiceNote> = persistentListOf(),
    val reminders: ImmutableList<Reminder> = persistentListOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isEdited: Boolean = false,

    val noteHistory: ImmutableList<NoteHistory> = persistentListOf(),

    val currentlyPlayingPath: String? = null,
    val isPlaying: Boolean = false,
    val currentPlaybackPosition: Long = 0L,
    val totalPlaybackDuration: Long = 0L,

    val clusterRootId: Long? = null,
    val clusterRootTitle: String? = null,

    val activeDialog: NoteEditorDialogState? = null
)