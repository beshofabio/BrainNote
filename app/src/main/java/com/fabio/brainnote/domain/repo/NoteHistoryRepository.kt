package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.NoteHistory
import kotlinx.coroutines.flow.Flow

interface NoteHistoryRepository {
    suspend fun insertHistory(history: NoteHistory)
    fun getHistoryForNote(noteId: Long): Flow<List<NoteHistory>>
}
