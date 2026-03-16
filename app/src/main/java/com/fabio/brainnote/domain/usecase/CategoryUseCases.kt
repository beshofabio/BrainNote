package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.category.GetAllCategoriesUseCase
import com.fabio.brainnote.domain.usecase.category.InitializeCategoriesUseCase

data class CategoryUseCases(
    val getAll: GetAllCategoriesUseCase,
    val initializeAll: InitializeCategoriesUseCase
)