package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.category.GetAllCategoriesUseCase

data class CategoryUseCases(
    val getAll: GetAllCategoriesUseCase
)