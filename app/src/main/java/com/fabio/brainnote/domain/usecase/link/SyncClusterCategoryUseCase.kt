package com.fabio.brainnote.domain.usecase.link

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.model.Category
import com.fabio.brainnote.domain.model.NoteHistory
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncClusterCategoryUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val historyRepository: NoteHistoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(rootNoteId: Long, newCategory: Category): Int =
        withContext(ioDispatcher) {
            val clusterNotes = noteRepository.getEntireNoteFullDetails(rootNoteId).first()

            clusterNotes.forEach { note ->
                noteRepository.upsertNote(note.copy(category = newCategory))

                historyRepository.insertHistory(
                    NoteHistory(
                        noteId = note.id,
                        title = note.title,
                        changeSummary = "Category changed to \"${newCategory.name}\" (synced from cluster)",
                        category = newCategory,
                        colorPriority = note.colorPriority,
                        modifiedAt = System.currentTimeMillis()
                    )
                )
            }

            clusterNotes.size
        }
}