package com.fabio.brainnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = NoteEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = [CategoryEntity.COLUMN_ID],
            childColumns = [NoteEntity.COLUMN_CATEGORY_ID],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(NoteEntity.COLUMN_CATEGORY_ID)]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val categoryId: Long?,
    val colorPriority: Int,
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val imagePath: String? = null
) {
    companion object {
        const val TABLE_NAME = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_CATEGORY_ID = "categoryId"
    }
}
