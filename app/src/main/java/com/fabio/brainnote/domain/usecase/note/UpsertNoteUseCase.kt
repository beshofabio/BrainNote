package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpsertNoteUseCase(
    private val repository: NoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(note: Note): Long {
        return withContext(ioDispatcher) {
            repository.upsertNote(note)
        }
    }
}