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

    @Query("DELETE FROM note_link WHERE noteId = :noteId AND linkedToId = :linkedId")
    suspend fun deleteLink(noteId: Long, linkedId: Long)
}