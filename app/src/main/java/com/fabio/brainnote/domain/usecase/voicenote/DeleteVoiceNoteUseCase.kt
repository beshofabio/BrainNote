package com.fabio.brainnote.domain.usecase.voicenote

import com.fabio.brainnote.domain.repo.VoiceNoteRepository

class DeleteVoiceNoteUseCase(
    private val repository: VoiceNoteRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteVoiceNote(id)
    }
}