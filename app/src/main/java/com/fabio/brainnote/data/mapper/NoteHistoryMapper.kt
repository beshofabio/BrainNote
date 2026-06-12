package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.model.NoteHistoryEntity
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.NoteHistory

fun NoteHistoryEntity.toDomain(category: Category?) = NoteHistory(
    id = id,
    noteId = noteId,
    title = title,
    changeSummary = changeSummary,
    category = category,
    colorPriority = colorPriority,
    modifiedAt = modifiedAt
)

fun NoteHistory.toEntity() = NoteHistoryEntity(
    id = id,
    noteId = noteId,
    title = title,
    changeSummary = changeSummary,
    categoryId = category?.id,
    colorPriority = colorPriority,
    modifiedAt = modifiedAt
)
