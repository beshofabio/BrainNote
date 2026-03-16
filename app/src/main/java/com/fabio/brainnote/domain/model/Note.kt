package com.fabio.brainnote.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: Category?,
    val colorPriority: Int,
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val imagePath: String? = null,

    val checklist: List<ChecklistItem> = emptyList(),
    val voiceNotes: List<VoiceNote> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val linkedNotes: List<LinkedNote> = emptyList()

)

val fakeNote = Note(
    id = 1,
    title = "Homework",
    content = "Find 10 videos about illustrations to share with classmates next week.",
    category = Category(
        id = 1,
        name = "Study",
        color = 0xFFFFC34D,
        icon = "ic_school"
    ),
    colorPriority = 0xFFFFC34D.toInt(),
    isPinned = true,
    isLocked = false,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
    imagePath = null,

    checklist = listOf(
        ChecklistItem(
            id = 1,
            text = "Search YouTube",
            isChecked = true,
            position = 0
        ),
        ChecklistItem(
            id = 2,
            text = "Check Instagram references",
            isChecked = false,
            position = 1
        ),
        ChecklistItem(
            id = 3,
            text = "Look for Twitter threads",
            isChecked = false,
            position = 2
        )
    ),

    voiceNotes = listOf(
        VoiceNote(
            id = 1,
            audioPath = "/storage/emulated/0/BrainNote/audio_1.m4a",
            duration = 32000
        )
    ),

    reminders = listOf(
        Reminder(
            id = 1,
            triggerTime = System.currentTimeMillis() + 86400000,
            repeatType = null
        )
    ),

    linkedNotes = emptyList()
)