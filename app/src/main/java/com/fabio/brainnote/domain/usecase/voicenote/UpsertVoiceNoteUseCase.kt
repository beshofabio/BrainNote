package com.fabio.brainnote.domain.usecase.voicenote

import com.fabio.brainnote.domain.model.VoiceNote
import com.fabio.brainnote.domain.repo.VoiceNoteRepository

class UpsertVoiceNoteUseCase(
    private val repository: VoiceNoteRepository
) {
    suspend operator fun invoke(voiceNote: VoiceNote, noteId : Long): Long {
        return repository.upsertVoiceNote(voiceNote, noteId)
    }
}