package com.fabio.brainnote.data.model

import androidx.room.*

@Entity(
    tableName = ChecklistItemEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [ChecklistItemEntity.COLUMN_NOTE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(ChecklistItemEntity.COLUMN_NOTE_ID)]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val text: String,
    val isChecked: Boolean = false,
    val position: Int
) {
    companion object {
        const val TABLE_NAME = "checklist_items"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTE_ID = "noteId"
    }
}
