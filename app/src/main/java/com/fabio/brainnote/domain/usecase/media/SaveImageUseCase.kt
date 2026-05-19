package com.fabio.brainnote.domain.usecase.media

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveImageUseCase @Inject constructor(
    private val repository: ImageStorageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(uriString: String): String? = withContext(ioDispatcher) {
        repository.copyImageToInternalStorage(uriString)
    }
}