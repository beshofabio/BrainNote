package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fabio.brainnote.data.datamodel.FullNoteDetails
import com.fabio.brainnote.data.model.NoteEntity
import com.fabio.brainnote.data.model.NoteLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query(
        """
        SELECT * FROM ${NoteEntity.TABLE_NAME}
        WHERE ${NoteEntity.COLUMN_ID} = :noteId
    """
    )
    fun getNoteFullDetails(noteId: Long): FullNoteDetails?

    @Transaction
    @Query("""
        SELECT * FROM ${NoteEntity.TABLE_NAME}
        WHERE ${NoteEntity.COLUMN_CATEGORY_ID} = :catId
        AND ${NoteEntity.COLUMN_ID} NOT IN (
            SELECT ${NoteLinkEntity.COLUMN_LINKED_TO_ID}
            FROM ${NoteLinkEntity.TABLE_NAME}
        )
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    fun getNotesFullDetailsByCategory(catId: Long): Flow<List<FullNoteDetails>>

    @Transaction
    @Query(
        """
        SELECT * FROM ${NoteEntity.TABLE_NAME} 
        WHERE ${NoteEntity.COLUMN_ID} = :rootId
        OR ${NoteEntity.COLUMN_ID} IN (
            SELECT ${NoteLinkEntity.COLUMN_LINKED_TO_ID} 
            FROM ${NoteLinkEntity.TABLE_NAME} 
            WHERE ${NoteLinkEntity.COLUMN_NOTE_ID} = :rootId
        )
    """
    )
    fun getClusterNotes(rootId: Long): Flow<List<FullNoteDetails>>

    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    @Query("DELETE FROM ${NoteEntity.TABLE_NAME} WHERE ${NoteEntity.COLUMN_ID} = :id")
    suspend fun deleteNote(id: Long)
}