package com.fabio.brainnote.domain.usecase.link

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncClusterPinUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val historyRepository: NoteHistoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(rootNoteId: Long, isPinned: Boolean): Int =
        withContext(ioDispatcher) {
            val clusterNotes = noteRepository.getEntireNoteFullDetails(rootNoteId).first()

            clusterNotes.forEach { note ->
                noteRepository.upsertNote(note.copy(isPinned = isPinned))

                val action = if (isPinned) "Pinned" else "Unpinned"
                historyRepository.insertHistory(
                    NoteHistory(
                        noteId = note.id,
                        title = note.title,
                        changeSummary = "$action note (synced from cluster)",
                        category = note.category,
                        colorPriority = note.colorPriority,
                        modifiedAt = System.currentTimeMillis()
                    )
                )
            }

            clusterNotes.size
        }
}