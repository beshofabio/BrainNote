package com.fabio.brainnote.domain.usecase.link

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.NoteLinkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertNoteLinkUseCase @Inject constructor(
    private val repository: NoteLinkRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(noteId: Long, linkedToId: Long) = withContext(ioDispatcher) {
        repository.insertLink(noteId, linkedToId)
    }
}