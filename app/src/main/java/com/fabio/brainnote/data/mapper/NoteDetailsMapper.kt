package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.datamodel.FullNoteDetails
import com.fabio.brainnote.data.model.NoteEntity
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.LinkedNote
import com.fabio.brainnote.domain.model.Note

fun FullNoteDetails.toDomain(): Note {

    return note.toDomain(category?.toDomain()).copy(
        checklist = checklistItems.toDomain(),
        voiceNotes = voiceNotes.toDomain(),
        reminders = reminders.toDomain(),
        linkedNotes = linkedNotes.toLinkedDomain()
    )
}

fun List<FullNoteDetails>.toDomain() = map { it.toDomain() }

fun NoteEntity.toDomain(category: Category?) = Note(
    id = id,
    title = title,
    content = content,
    category = category,
    colorPriority = colorPriority,
    isPinned = isPinned,
    isLocked = isLocked,
    createdAt = createdAt,
    updatedAt = updatedAt,
    imagePath = imagePath
)

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        categoryId = category?.id,
        colorPriority = colorPriority,
        isPinned = isPinned,
        isLocked = isLocked,
        createdAt = createdAt,
        updatedAt = updatedAt,
        imagePath = imagePath
    )
}

fun NoteEntity.toLinkedDomain() = LinkedNote(
    id = id,
    title = title,
    imagePath = imagePath
)

fun List<NoteEntity>.toLinkedDomain() = map { it.toLinkedDomain() }


