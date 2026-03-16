package com.fabio.brainnote.domain.model

data class ChecklistItem(
    val id: Long = 0,
    val text: String,
    val isChecked: Boolean = false,
    val position: Int
)