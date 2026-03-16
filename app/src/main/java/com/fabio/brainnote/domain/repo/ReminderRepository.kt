package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.Reminder

interface ReminderRepository {
    suspend fun upsertReminder(reminder: Reminder, noteId : Long): Long

    suspend fun deleteReminder(id: Long)
}