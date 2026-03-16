package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.model.CategoryEntity
import com.fabio.brainnote.domain.model.Category

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    color = color,
    icon = icon
)
fun List<CategoryEntity>.toDomain() = map { it.toDomain() }

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    color = color,
    icon = icon
)
fun List<Category>.toEntity() = map { it.toEntity() }
