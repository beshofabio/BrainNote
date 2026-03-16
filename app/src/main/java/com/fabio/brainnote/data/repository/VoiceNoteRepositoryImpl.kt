package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.VoiceNoteDao
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.VoiceNote
import com.fabio.brainnote.domain.repo.VoiceNoteRepository

class VoiceNoteRepositoryImpl(
    private val dao: VoiceNoteDao
) : VoiceNoteRepository {

    override suspend fun upsertVoiceNote(voiceNote: VoiceNote, noteId : Long): Long {
        return dao.upsertVoiceNote(voiceNote.toEntity(noteId))
    }

    override suspend fun deleteVoiceNote(id: Long) {
        dao.deleteVoiceNote(id)
    }
}