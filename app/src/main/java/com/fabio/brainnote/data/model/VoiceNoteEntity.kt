package com.fabio.brainnote.data.model

import androidx.room.*

@Entity(
    tableName = VoiceNoteEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [VoiceNoteEntity.COLUMN_NOTE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(VoiceNoteEntity.COLUMN_NOTE_ID)]
)
data class VoiceNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val audioPath: String,
    val duration: Long
) {
    companion object {
        const val TABLE_NAME = "voice_notes"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTE_ID = "noteId"
    }
}
