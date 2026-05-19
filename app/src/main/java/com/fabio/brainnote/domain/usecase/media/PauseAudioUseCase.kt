package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import javax.inject.Inject

class PauseAudioUseCase @Inject constructor(
    private val repository: AudioPlayerRepository
) {
    operator fun invoke() {
        repository.pause()
    }
}
