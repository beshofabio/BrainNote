package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM ${ReminderEntity.TABLE_NAME} WHERE ${ReminderEntity.COLUMN_NOTE_ID} = :noteId")
    fun getRemindersForNote(noteId: Long): Flow<List<ReminderEntity>>

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity): Long

    @Query("DELETE FROM ${ReminderEntity.TABLE_NAME} WHERE ${ReminderEntity.COLUMN_ID} = :id")
    suspend fun deleteReminder(id: Long)
}
