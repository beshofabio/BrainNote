package com.fabio.brainnote.domain.model

import androidx.annotation.DrawableRes

data class Category(
    val id: Long = 0,
    val name: String,
    val color: Long,
    @DrawableRes val icon: Int
)