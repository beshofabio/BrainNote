package com.fabio.brainnote.data.mapper

import com.fabio.brainnote.data.model.ReminderEntity
import com.fabio.brainnote.domain.model.Reminder

fun ReminderEntity.toDomain() = Reminder(
    id = id,
    triggerTime = triggerTime,
    repeatType = repeatType
)
fun List<ReminderEntity>.toDomain() = map { it.toDomain() }

fun Reminder.toEntity(noteId: Long) = ReminderEntity(
    id = id,
    noteId = noteId,
    triggerTime = triggerTime,
    repeatType = repeatType
)
fun List<Reminder>.toEntity(noteId: Long) = map { it.toEntity(noteId) }
