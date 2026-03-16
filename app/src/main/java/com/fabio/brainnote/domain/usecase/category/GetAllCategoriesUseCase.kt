package com.fabio.brainnote.domain.usecase.category

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.repo.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetAllCategoriesUseCase(
    private val repository: CategoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository
            .getAllCategories()
            .flowOn(ioDispatcher)
    }
}