package com.fabio.brainnote.domain.usecase.note

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.AlarmScheduler
import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val imageRepository: ImageStorageRepository,
    private val audioRepository: AudioRecorderRepository,
    private val alarmScheduler: AlarmScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(noteId: Long) = withContext(ioDispatcher) {
        val note = noteRepository.getNoteById(noteId)
        if (note != null) {
            note.reminders.forEach { reminder ->
                alarmScheduler.cancel(reminder)
            }

            note.imagePath?.let { imageRepository.deleteImage(it) }

            note.voiceNotes.forEach { voiceNote ->
                audioRepository.deleteFile(voiceNote.audioPath)
            }

            noteRepository.deleteNote(noteId)
        }
    }
}
