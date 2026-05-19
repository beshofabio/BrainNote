package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import com.fabio.brainnote.domain.repo.PlaybackProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaybackProgressUseCase @Inject constructor(
    private val repository: AudioPlayerRepository
) {
    operator fun invoke(): Flow<PlaybackProgress> {
        return repository.playbackProgress
    }
}
