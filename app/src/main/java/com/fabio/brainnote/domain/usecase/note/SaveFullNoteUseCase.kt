package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Note
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.repo.AlarmScheduler
import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import com.fabio.brainnote.domain.repo.ChecklistRepository
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import com.fabio.brainnote.domain.repo.VoiceNoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveFullNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val historyRepository: NoteHistoryRepository,
    private val reminderRepository: ReminderRepository,
    private val voiceNoteRepository: VoiceNoteRepository,
    private val checklistRepository: ChecklistRepository,
    private val imageRepository: ImageStorageRepository,
    private val audioRepository: AudioRecorderRepository,
    private val alarmScheduler: AlarmScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(noteToSave: Note, summary: String): Long = withContext(ioDispatcher) {
        if (noteToSave.id != 0L) {
            val existingNote = noteRepository.getNoteById(noteToSave.id)
            if (existingNote != null) {

                if (existingNote.imagePath != null && noteToSave.imagePath == null) {
                    imageRepository.deleteImage(existingNote.imagePath)
                }

                val incomingReminderIds = noteToSave.reminders.map { it.id }.toSet()
                existingNote.reminders.forEach { oldReminder ->
                    if (oldReminder.id !in incomingReminderIds) {
                        alarmScheduler.cancel(oldReminder)
                        reminderRepository.deleteReminder(oldReminder.id)
                    }
                }

                val incomingVoicePaths = noteToSave.voiceNotes.map { it.audioPath }.toSet()
                existingNote.voiceNotes.forEach { oldVoice ->
                    if (oldVoice.audioPath !in incomingVoicePaths) {
                        audioRepository.deleteFile(oldVoice.audioPath)
                        voiceNoteRepository.deleteVoiceNote(oldVoice.id)
                    }
                }

                val incomingChecklistIds = noteToSave.checklist.map { it.id }.toSet()
                existingNote.checklist.forEach { oldItem ->
                    if (oldItem.id !in incomingChecklistIds) {
                        checklistRepository.deleteItem(oldItem.id)
                    }
                }
            }
        }

        val upsertedResultId = noteRepository.upsertNote(noteToSave)
        val finalNoteId = if (upsertedResultId == -1L) noteToSave.id else upsertedResultId

        noteToSave.reminders.forEach { reminder ->
            val databaseReminderId = reminderRepository.upsertReminder(reminder, finalNoteId)
            val finalReminder = if (reminder.id == 0L) reminder.copy(id = databaseReminderId) else reminder
            alarmScheduler.schedule(
                reminder = finalReminder,
                noteTitle = noteToSave.title,
                noteContent = noteToSave.content
            )
        }

        noteToSave.voiceNotes.forEach { voiceNote ->
            voiceNoteRepository.upsertVoiceNote(voiceNote, finalNoteId)
        }

        noteToSave.checklist.forEach { item ->
            checklistRepository.upsertItem(item, finalNoteId)
        }

        val historyEntry = NoteHistory(
            noteId = finalNoteId,
            title = noteToSave.title,
            changeSummary = summary,
            category = noteToSave.category,
            colorPriority = noteToSave.colorPriority,
            modifiedAt = System.currentTimeMillis()
        )
        historyRepository.insertHistory(historyEntry)

        return@withContext finalNoteId
    }
}