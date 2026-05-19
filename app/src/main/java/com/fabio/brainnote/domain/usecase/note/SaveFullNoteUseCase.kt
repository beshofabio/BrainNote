package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.repo.ChecklistRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import com.fabio.brainnote.domain.repo.VoiceNoteRepository
import javax.inject.Inject

class SaveFullNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val checklistRepository: ChecklistRepository,
    private val voiceNoteRepository: VoiceNoteRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(note: Note) {
        val savedNoteId = noteRepository.upsertNote(note)
        val finalNoteId = if (note.id == 0L) savedNoteId else note.id
        note.checklist.forEach { item ->
            checklistRepository.upsertItem(item, finalNoteId)
        }
        note.voiceNotes.forEach { voice ->
            voiceNoteRepository.upsertVoiceNote(voice, finalNoteId)
        }
        note.reminders.forEach { reminder ->
            reminderRepository.upsertReminder(reminder, finalNoteId)
        }
    }
}