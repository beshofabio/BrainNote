package com.fabio.brainnote.domain.usecase.note

import android.graphics.Bitmap
import com.fabio.brainnote.data.local.ImageStorage
import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveNoteImageUseCase(
    private val repository: NoteRepository,
    private val imageStorage: ImageStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(noteId: Long, bitmap: Bitmap) = withContext(ioDispatcher) {
        val path = imageStorage.saveImage(bitmap)
        repository.updateNoteImage(noteId, path)
    }
}