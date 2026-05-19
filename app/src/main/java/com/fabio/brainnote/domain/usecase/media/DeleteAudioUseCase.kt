package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import javax.inject.Inject

class DeleteAudioUseCase @Inject constructor(
    private val repository: AudioRecorderRepository
) {
    operator fun invoke(path: String) {
        repository.deleteFile(path)
    }
}