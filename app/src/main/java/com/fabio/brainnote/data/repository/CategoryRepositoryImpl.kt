package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.mapper.toDomain
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.repo.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories()
            .map { list -> list.toDomain() }
    }

    override suspend fun upsertCategories(categories: List<Category>): List<Long> {
        return dao.upsertCategory(categories.toEntity())
    }
    override suspend fun deleteCategory(id: Long) {
        dao.deleteCategory(id)
    }
}