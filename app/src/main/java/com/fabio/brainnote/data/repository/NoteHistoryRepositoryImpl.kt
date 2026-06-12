package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.dao.NoteHistoryDao
import com.fabio.brainnote.data.mapper.toDomain
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class NoteHistoryRepositoryImpl @Inject constructor(
    private val historyDao: NoteHistoryDao,
    private val categoryDao: CategoryDao
) : NoteHistoryRepository {

    override suspend fun insertHistory(history: NoteHistory) {
        historyDao.insertHistory(history.toEntity())
    }

    override fun getHistoryForNote(noteId: Long): Flow<List<NoteHistory>> {
        return historyDao.getHistoryForNote(noteId).combine(categoryDao.getAllCategories()) { historyEntities, categoryEntities ->
            historyEntities.map { entity ->
                val category = categoryEntities.find { it.id == entity.categoryId }?.toDomain()
                entity.toDomain(category)
            }
        }
    }
}
