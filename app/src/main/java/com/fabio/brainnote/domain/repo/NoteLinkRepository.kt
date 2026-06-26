package com.fabio.brainnote.domain.repo

interface NoteLinkRepository {
    suspend fun insertLink(noteId: Long, linkedToId: Long)
    suspend fun deleteLink(noteId: Long, linkedToId: Long)
}