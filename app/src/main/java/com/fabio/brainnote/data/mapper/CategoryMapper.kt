package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.R
import com.fabio.brainnote.data.model.CategoryEntity
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.CategoryType

private fun getIconFromName(name: String): Int {
    return when (name.uppercase()) {
        CategoryType.WORK.name -> R.drawable.work
        CategoryType.IDEAS.name -> R.drawable.ideas
        CategoryType.PLANS.name -> R.drawable.plans
        CategoryType.BIRTHDAY.name -> R.drawable.birthday
        else -> R.drawable.others
    }
}

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    color = color,
    icon = getIconFromName(name)
)
fun List<CategoryEntity>.toDomain() = map { it.toDomain() }

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    color = color,
)
fun List<Category>.toEntity() = map { it.toEntity() }
