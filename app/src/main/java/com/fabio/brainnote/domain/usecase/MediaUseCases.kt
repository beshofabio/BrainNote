package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.media.*

data class MediaUseCases(
    val saveImage: SaveImageUseCase,
    val startAudio: StartAudioUseCase,
    val stopAudio: StopAudioUseCase,
    val deleteAudio: DeleteAudioUseCase,
    val playAudio: PlayAudioUseCase,
    val pauseAudio: PauseAudioUseCase,
    val resumeAudio: ResumeAudioUseCase,
    val seekAudio: SeekAudioUseCase,
    val getPlaybackProgress: GetPlaybackProgressUseCase
)
