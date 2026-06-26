package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.NoteLinkDao
import com.fabio.brainnote.data.model.NoteLinkEntity
import com.fabio.brainnote.domain.repo.NoteLinkRepository
import javax.inject.Inject

class NoteLinkRepositoryImpl @Inject constructor(
    private val dao: NoteLinkDao
) : NoteLinkRepository {

    override suspend fun insertLink(noteId: Long, linkedToId: Long) {
        dao.insertLink(NoteLinkEntity(noteId = noteId, linkedToId = linkedToId))
    }

    override suspend fun deleteLink(noteId: Long, linkedToId: Long) {
        dao.deleteLink(noteId = noteId, linkedId = linkedToId)
    }
}