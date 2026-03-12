package com.fabio.brainnote.data.model

import androidx.room.*

@Entity(
    tableName = "note_history",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class NoteHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val title: String,
    val content: String,
    val categoryId: Long?,
    val colorPriority: Int,
    val modifiedAt: Long
)