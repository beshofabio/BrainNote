package com.fabio.brainnote.domain.model

data class ClusterInformation(
    val head: Note,
    val childrenCount: Int,
    val category: Category?
)