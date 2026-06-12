package com.fabio.brainnote.data.model

import androidx.room.*

@Entity(
    tableName = NoteHistoryEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [NoteHistoryEntity.COLUMN_NOTE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(NoteHistoryEntity.COLUMN_NOTE_ID)]
)
data class NoteHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val title: String,
    val changeSummary: String,
    val categoryId: Long?,
    val colorPriority: Int,
    val modifiedAt: Long
) {
    companion object {
        const val TABLE_NAME = "note_history"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTE_ID = "noteId"
    }
}
