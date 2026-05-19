package com.fabio.brainnote.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fabio.brainnote.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} ORDER BY ${CategoryEntity.COLUMN_ID}")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.COLUMN_ID} = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Upsert
    suspend fun upsertCategory(categories: List<CategoryEntity>): List<Long>

    @Query("DELETE FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.COLUMN_ID} = :id")
    suspend fun deleteCategory(id: Long)
}
