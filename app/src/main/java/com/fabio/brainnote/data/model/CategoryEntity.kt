package com.fabio.brainnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Long,
) {
    companion object {
        const val TABLE_NAME = "categories"
        const val COLUMN_ID = "id"
    }
}
