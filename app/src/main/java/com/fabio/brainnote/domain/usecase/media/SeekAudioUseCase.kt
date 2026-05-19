package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import javax.inject.Inject

class SeekAudioUseCase @Inject constructor(
    private val repository: AudioPlayerRepository
) {
    operator fun invoke(position: Long) {
        repository.seekTo(position)
    }
}
