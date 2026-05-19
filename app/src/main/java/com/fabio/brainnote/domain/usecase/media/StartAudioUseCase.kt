package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import javax.inject.Inject

class StartAudioUseCase @Inject constructor(
    private val repository: AudioRecorderRepository
) {
    operator fun invoke(): String? {
        return repository.startRecording()
    }
}