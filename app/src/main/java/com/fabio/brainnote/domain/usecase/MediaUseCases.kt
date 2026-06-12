package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.media.*
import javax.inject.Inject

class MediaUseCases @Inject constructor(
    val saveImage: SaveImageUseCase,
    val deleteImage: DeleteImageUseCase,
    val startAudio: StartAudioUseCase,
    val stopAudio: StopAudioUseCase,
    val deleteAudio: DeleteAudioUseCase,
    val playAudio: PlayAudioUseCase,
    val pauseAudio: PauseAudioUseCase,
    val resumeAudio: ResumeAudioUseCase,
    val seekAudio: SeekAudioUseCase,
    val getPlaybackProgress: GetPlaybackProgressUseCase
)