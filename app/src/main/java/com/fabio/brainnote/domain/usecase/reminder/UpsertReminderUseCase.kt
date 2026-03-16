package com.fabio.brainnote.domain.usecase.reminder

import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.repo.ReminderRepository

class UpsertReminderUseCase(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder, noteId : Long): Long {
        return repository.upsertReminder(reminder, noteId)
    }
}