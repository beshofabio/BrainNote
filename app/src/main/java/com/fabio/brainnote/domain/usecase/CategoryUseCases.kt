package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.category.GetAllCategoriesUseCase
import javax.inject.Inject

class CategoryUseCases @Inject constructor(
    val getAll: GetAllCategoriesUseCase
)