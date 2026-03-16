package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getEntireNoteFullDetails(rootId: Long): Flow<List<Note>>

    suspend fun upsertNote(note: Note): Long

    suspend fun updateNoteImage(noteId: Long, path: String?)
}