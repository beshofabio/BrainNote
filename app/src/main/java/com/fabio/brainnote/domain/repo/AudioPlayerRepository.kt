package com.fabio.brainnote.domain.repo

import kotlinx.coroutines.flow.Flow

interface AudioPlayerRepository {
    fun play(path: String)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Long)
    
    val playbackProgress: Flow<PlaybackProgress>
}

data class PlaybackProgress(
    val currentPosition: Long,
    val duration: Long,
    val isPlaying: Boolean
)
