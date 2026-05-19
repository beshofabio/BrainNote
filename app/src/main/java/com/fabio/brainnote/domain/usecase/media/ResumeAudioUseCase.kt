package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import javax.inject.Inject

class ResumeAudioUseCase @Inject constructor(
    private val repository: AudioPlayerRepository
) {
    operator fun invoke() {
        repository.resume()
    }
}
