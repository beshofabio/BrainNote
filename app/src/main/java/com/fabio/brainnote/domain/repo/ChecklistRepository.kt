package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    suspend fun upsertItem(item: ChecklistItem, noteId : Long): Long

    suspend fun deleteItem(id: Long)
}