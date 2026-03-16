package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.model.ChecklistItemEntity
import com.fabio.brainnote.domain.model.ChecklistItem

fun ChecklistItemEntity.toDomain() = ChecklistItem(
    id = id,
    text = text,
    isChecked = isChecked,
    position = position
)
fun List<ChecklistItemEntity>.toDomain() = map { it.toDomain() }


fun ChecklistItem.toEntity(noteId : Long) = ChecklistItemEntity(
    id = id,
    text = text,
    isChecked = isChecked,
    position = position,
    noteId = noteId
)
fun List<ChecklistItem>.toEntity(noteId : Long) = map { it.toEntity(noteId) }
