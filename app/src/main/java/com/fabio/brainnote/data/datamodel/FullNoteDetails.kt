package com.fabio.brainnote.data.datamodel

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fabio.brainnote.data.model.CategoryEntity
import com.fabio.brainnote.data.model.ChecklistItemEntity
import com.fabio.brainnote.data.model.NoteEntity
import com.fabio.brainnote.data.model.NoteLinkEntity
import com.fabio.brainnote.data.model.ReminderEntity
import com.fabio.brainnote.data.model.VoiceNoteEntity

data class FullNoteDetails(
    @Embedded val note: NoteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val checklistItems: List<ChecklistItemEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val voiceNotes: List<VoiceNoteEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val reminders: List<ReminderEntity>,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteLinkEntity::class,
            parentColumn = "noteId",
            entityColumn = "linkedToId"
        )
    )
    val linkedNotes: List<NoteEntity>
)