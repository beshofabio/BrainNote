package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val repository: ImageStorageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(path: String) = withContext(ioDispatcher) {
        repository.deleteImage(path)
    }
}