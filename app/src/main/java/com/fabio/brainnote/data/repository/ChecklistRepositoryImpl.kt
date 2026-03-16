package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.ChecklistItemDao
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.repo.ChecklistRepository

class ChecklistRepositoryImpl(
    private val dao: ChecklistItemDao
) : ChecklistRepository {

    override suspend fun upsertItem(item: ChecklistItem, noteId : Long): Long {
        return dao.upsertItem(item.toEntity(noteId))
    }

    override suspend fun deleteItem(id: Long) {
        dao.deleteItem(id)
    }
}