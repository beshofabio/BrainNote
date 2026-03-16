package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.VoiceNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceNoteDao {

    @Query("SELECT * FROM voice_notes WHERE noteId = :noteId")
    fun getVoiceNotesForNote(noteId: Long): Flow<List<VoiceNoteEntity>>

    @Upsert
    suspend fun upsertVoiceNote(voiceNote: VoiceNoteEntity): Long

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteVoiceNote(id: Long)
}