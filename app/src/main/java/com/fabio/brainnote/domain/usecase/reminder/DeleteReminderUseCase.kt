package com.fabio.brainnote.domain.usecase.reminder

import com.fabio.brainnote.domain.repo.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteReminder(id)
    }
}