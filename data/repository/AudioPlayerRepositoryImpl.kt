package com.fabio.brainnote.data.repository

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import com.fabio.brainnote.domain.repo.PlaybackProgress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioPlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPlayerRepository {

    private var player: MediaPlayer? = null
    private val _playbackProgress = MutableStateFlow(PlaybackProgress(0, 0, false))
    override val playbackProgress: StateFlow<PlaybackProgress> = _playbackProgress.asStateFlow()

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun play(path: String) {
        player?.stop()
        player?.release()
        
        player = MediaPlayer.create(context, path.toUri()).apply {
            start()
            _playbackProgress.value = PlaybackProgress(0, duration.toLong(), true)
            
            setOnCompletionListener {
                stopProgressUpdate()
                _playbackProgress.value = _playbackProgress.value.copy(isPlaying = false, currentPosition = duration.toLong())
            }
        }
        startProgressUpdate()
    }

    override fun pause() {
        player?.pause()
        stopProgressUpdate()
        _playbackProgress.value = _playbackProgress.value.copy(isPlaying = false)
    }

    override fun resume() {
        player?.start()
        startProgressUpdate()
        _playbackProgress.value = _playbackProgress.value.copy(isPlaying = true)
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
        stopProgressUpdate()
        _playbackProgress.value = PlaybackProgress(0, 0, false)
    }

    override fun seekTo(position: Long) {
        player?.seekTo(position.toInt())
        _playbackProgress.value = _playbackProgress.value.copy(currentPosition = position)
    }

    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressJob = scope.launch {
            while (true) {
                player?.let {
                    if (it.isPlaying) {
                        _playbackProgress.value = _playbackProgress.value.copy(
                            currentPosition = it.currentPosition.toLong()
                        )
                    }
                }
                // Updated to 32ms for smoother UI updates (approx 30fps)
                delay(32)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }
}
