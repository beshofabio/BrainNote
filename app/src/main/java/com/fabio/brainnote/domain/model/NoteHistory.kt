package com.fabio.brainnote.domain.model

data class NoteHistory(
    val id: Long = 0,
    val noteId: Long,
    val title: String,
    val changeSummary: String,
    val category: Category?,
    val colorPriority: Int,
    val modifiedAt: Long
)
