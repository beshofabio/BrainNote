package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetNoteHistoryUseCase @Inject constructor(
    private val repository: NoteHistoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(noteId: Long): Flow<List<NoteHistory>> {
        return repository.getHistoryForNote(noteId).flowOn(ioDispatcher)
    }
}
