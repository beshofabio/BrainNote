package com.fabio.brainnote.data.model

import androidx.room.*
import com.fabio.brainnote.domain.RepeatType

@Entity(
    tableName = ReminderEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = [NoteEntity.COLUMN_ID],
            childColumns = [ReminderEntity.COLUMN_NOTE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(ReminderEntity.COLUMN_NOTE_ID)]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val triggerTime: Long,
    val repeatType: RepeatType?
) {
    companion object {
        const val TABLE_NAME = "reminders"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTE_ID = "noteId"
    }
}
