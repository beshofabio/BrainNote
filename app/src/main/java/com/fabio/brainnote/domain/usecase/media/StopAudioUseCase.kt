package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import javax.inject.Inject

class StopAudioUseCase @Inject constructor(
    private val repository: AudioRecorderRepository
) {
    operator fun invoke() {
        repository.stopRecording()
    }
}