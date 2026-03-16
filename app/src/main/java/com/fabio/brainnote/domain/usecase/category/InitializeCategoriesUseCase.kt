package com.fabio.brainnote.domain.usecase.category

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.repo.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InitializeCategoriesUseCase(
    private val repository: CategoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        val defaultCategories = listOf(
            Category(name = "Work", color = 0xFF4285F4, icon = "ic_work"),
            Category(name = "Ideas", color = 0xFFFBBC05, icon = "ic_lightbulb"),
            Category(name = "Plans", color = 0xFF34A853, icon = "ic_event"),
            Category(name = "Birthday", color = 0xFFEA4335, icon = "ic_cake"),
            Category(name = "Others", color = 0xFF70757A, icon = "ic_more")
        )

        repository.upsertCategories(defaultCategories)
    }
}