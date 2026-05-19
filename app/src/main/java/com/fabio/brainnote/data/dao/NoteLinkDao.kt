package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fabio.brainnote.data.model.NoteLinkEntity

@Dao
interface NoteLinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: NoteLinkEntity)

    @Query("""
        DELETE FROM ${NoteLinkEntity.TABLE_NAME} 
        WHERE ${NoteLinkEntity.COLUMN_NOTE_ID} = :noteId 
        AND ${NoteLinkEntity.COLUMN_LINKED_TO_ID} = :linkedId
    """)
    suspend fun deleteLink(noteId: Long, linkedId: Long)
}
