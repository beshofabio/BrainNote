package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getAllCategories(): Flow<List<Category>>

    suspend fun upsertCategories(categories: List<Category>): List<Long>

    suspend fun deleteCategory(id: Long)
}