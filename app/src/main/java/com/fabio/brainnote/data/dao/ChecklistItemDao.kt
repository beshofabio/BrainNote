package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {

    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY position")
    fun getItemsForNote(noteId: Long): Flow<List<ChecklistItemEntity>>

    @Upsert
    suspend fun upsertItem(item: ChecklistItemEntity): Long

    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}