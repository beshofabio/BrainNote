package com.fabio.brainnote.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = NoteLinkEntity.TABLE_NAME,
    primaryKeys = [NoteLinkEntity.COLUMN_NOTE_ID, NoteLinkEntity.COLUMN_LINKED_TO_ID],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [NoteLinkEntity.COLUMN_NOTE_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [NoteLinkEntity.COLUMN_LINKED_TO_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(NoteLinkEntity.COLUMN_NOTE_ID), Index(NoteLinkEntity.COLUMN_LINKED_TO_ID)]
)
data class NoteLinkEntity(
    val noteId: Long,
    val linkedToId: Long
) {
    companion object {
        const val TABLE_NAME = "note_link"
        const val COLUMN_NOTE_ID = "noteId"
        const val COLUMN_LINKED_TO_ID = "linkedToId"
    }
}
