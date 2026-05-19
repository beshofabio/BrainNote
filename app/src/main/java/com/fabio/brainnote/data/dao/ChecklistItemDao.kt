package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM ${ChecklistItemEntity.TABLE_NAME} WHERE ${ChecklistItemEntity.COLUMN_NOTE_ID} = :noteId ORDER BY position")
    fun getItemsForNote(noteId: Long): Flow<List<ChecklistItemEntity>>

    @Upsert
    suspend fun upsertItem(item: ChecklistItemEntity): Long

    @Query("DELETE FROM ${ChecklistItemEntity.TABLE_NAME} WHERE ${ChecklistItemEntity.COLUMN_ID} = :id")
    suspend fun deleteItem(id: Long)
}
