package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val repository: AudioPlayerRepository
) {
    operator fun invoke(path: String) {
        repository.play(path)
    }
}
