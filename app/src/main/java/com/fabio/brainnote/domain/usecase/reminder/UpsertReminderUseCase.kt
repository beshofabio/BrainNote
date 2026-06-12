package com.fabio.brainnote.domain.usecase.reminder

import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.repo.AlarmScheduler
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import javax.inject.Inject

class UpsertReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val noteRepository: NoteRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(reminder: Reminder, noteId: Long): Long {
        val reminderId = reminderRepository.upsertReminder(reminder, noteId)
        
        val note = noteRepository.getNoteById(noteId)
        
        if (note != null) {
            val updatedReminder = reminder.copy(id = reminderId)
            alarmScheduler.schedule(
                reminder = updatedReminder,
                noteTitle = note.title,
                noteContent = note.content
            )
        }
        
        return reminderId
    }
}
