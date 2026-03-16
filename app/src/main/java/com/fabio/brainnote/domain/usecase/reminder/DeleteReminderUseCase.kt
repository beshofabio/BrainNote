package com.fabio.brainnote.domain.usecase.reminder

import com.fabio.brainnote.domain.repo.ReminderRepository

class DeleteReminderUseCase(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteReminder(id)
    }
}