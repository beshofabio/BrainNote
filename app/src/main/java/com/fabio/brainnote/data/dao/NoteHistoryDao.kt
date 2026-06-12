package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fabio.brainnote.data.model.NoteHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: NoteHistoryEntity)

    @Query("SELECT * FROM ${NoteHistoryEntity.TABLE_NAME} WHERE ${NoteHistoryEntity.COLUMN_NOTE_ID} = :noteId ORDER BY modifiedAt DESC")
    fun getHistoryForNote(noteId: Long): Flow<List<NoteHistoryEntity>>
}
