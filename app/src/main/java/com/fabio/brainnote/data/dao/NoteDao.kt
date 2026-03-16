package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fabio.brainnote.data.datamodel.FullNoteDetails
import com.fabio.brainnote.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE categoryId = :catId
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun getNotesFullDetailsByCategory(catId: Long): Flow<List<FullNoteDetails>>


    @Transaction
    @Query("""
        SELECT * FROM notes 
        WHERE id = :rootId
        OR id IN (SELECT linkedToId FROM note_link WHERE noteId = :rootId)
    """)
    fun getClusterNotes(rootId: Long): Flow<List<FullNoteDetails>>


    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long


    @Query("UPDATE notes SET imagePath = :path WHERE id = :noteId")
    suspend fun updateNoteImage(noteId: Long, path: String?)
}