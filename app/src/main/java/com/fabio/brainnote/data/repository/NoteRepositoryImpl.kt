package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.NoteDao
import com.fabio.brainnote.data.mapper.toDomain
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteFullDetails(noteId)?.toDomain()
    }

    override fun getNotesByCategory(categoryId: Long): Flow<List<Note>> {
        return noteDao.getNotesFullDetailsByCategory(categoryId)
            .map { it.toDomain() }
    }

    override fun getEntireNoteFullDetails(rootId: Long): Flow<List<Note>> {
        return noteDao.getClusterNotes(rootId)
            .map { it.toDomain() }
    }

    override suspend fun upsertNote(note: Note): Long {
        return noteDao.upsertNote(note.toEntity())
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteNote(id)
    }
}