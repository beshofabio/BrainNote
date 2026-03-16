package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE noteId = :noteId")
    fun getRemindersForNote(noteId: Long): Flow<List<ReminderEntity>>

    @Upsert
    suspend fun upsertReminder(reminder: ReminderEntity): Long

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}