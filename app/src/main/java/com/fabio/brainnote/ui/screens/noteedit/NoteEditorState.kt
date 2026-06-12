package com.fabio.brainnote.ui.screens.noteedit

import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.model.VoiceNote

data class NoteEditorState(
    val noteId: Long? = null,
    val title: String = "",
    val content: String = "",
    val imagePath: String? = null,
    val selectedCategory: Category? = null,
    val availableCategories: List<Category> = emptyList(),
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val colorPriority: Int = 0xFFFFC34D.toInt(),
    val checklists: List<ChecklistItem> = emptyList(),
    val voiceNotes: List<VoiceNote> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isEdited: Boolean = false,
    
    val noteHistory: List<NoteHistory> = emptyList(),
    
    val currentlyPlayingPath: String? = null,
    val isPlaying: Boolean = false,
    val currentPlaybackPosition: Long = 0L,
    val totalPlaybackDuration: Long = 0L
)
