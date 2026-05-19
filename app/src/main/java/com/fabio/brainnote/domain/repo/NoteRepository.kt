package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNoteById(noteId: Long): Note?
    fun getNotesByCategory(categoryId: Long): Flow<List<Note>>
    fun getEntireNoteFullDetails(rootId: Long): Flow<List<Note>>

    suspend fun upsertNote(note: Note): Long
}