package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetEntireClusterUseCase(
    private val repository: NoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(rootId: Long): Flow<List<Note>> {
        return repository
            .getEntireNoteFullDetails(rootId)
            .flowOn(ioDispatcher)
    }
}