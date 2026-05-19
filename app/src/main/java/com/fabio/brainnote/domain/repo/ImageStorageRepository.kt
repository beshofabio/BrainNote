package com.fabio.brainnote.domain.repo

interface ImageStorageRepository {
    suspend fun copyImageToInternalStorage(uriString: String): String?
}