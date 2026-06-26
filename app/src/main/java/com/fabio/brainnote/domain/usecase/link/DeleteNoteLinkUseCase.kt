package com.fabio.brainnote.domain.usecase.link


import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.NoteLinkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteNoteLinkUseCase @Inject constructor(
    private val repository: NoteLinkRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun unlinkSingle(noteId: Long, linkedToId: Long) = withContext(ioDispatcher) {
        repository.deleteLink(noteId, linkedToId)
    }

    suspend fun dissolveCluster(rootNoteId: Long, childrenIds: List<Long>) = withContext(ioDispatcher) {
        childrenIds.forEach { childId ->
            repository.deleteLink(noteId = rootNoteId, linkedToId = childId)
        }
    }
}