package com.fabio.brainnote.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class VoiceNote(
    val id: Long = 0,
    val audioPath: String,
    val duration: Long
)