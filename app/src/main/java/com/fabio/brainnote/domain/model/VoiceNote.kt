package com.fabio.brainnote.domain.model

data class VoiceNote(
    val id: Long = 0,
    val audioPath: String,
    val duration: Long
)