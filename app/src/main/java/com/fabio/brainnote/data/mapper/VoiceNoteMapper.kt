package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.model.VoiceNoteEntity
import com.fabio.brainnote.domain.model.VoiceNote

fun VoiceNoteEntity.toDomain() = VoiceNote(
    id = id,
    audioPath = audioPath,
    duration = duration
)
fun List<VoiceNoteEntity>.toDomain() = map { it.toDomain() }

fun VoiceNote.toEntity(noteId : Long) = VoiceNoteEntity(
    id = id,
    audioPath = audioPath,
    duration = duration,
    noteId = noteId
)
fun List<VoiceNote>.toEntity(noteId: Long) = map { it.toEntity(noteId) }