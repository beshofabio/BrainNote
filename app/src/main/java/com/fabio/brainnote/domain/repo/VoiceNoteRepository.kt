package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.VoiceNote
import kotlinx.coroutines.flow.Flow

interface VoiceNoteRepository {

    suspend fun upsertVoiceNote(voiceNote: VoiceNote, noteId : Long): Long

    suspend fun deleteVoiceNote(id: Long)
}