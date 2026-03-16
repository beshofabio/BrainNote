package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Upsert
    suspend fun upsertCategory(categories: List<CategoryEntity>): List<Long>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)
}