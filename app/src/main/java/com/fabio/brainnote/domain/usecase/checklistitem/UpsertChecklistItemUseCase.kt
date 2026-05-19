package com.fabio.brainnote.domain.usecase.checklistitem

import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.domain.repo.ChecklistRepository
import javax.inject.Inject

class UpsertChecklistItemUseCase @Inject constructor(
    private val repository: ChecklistRepository
) {

    suspend operator fun invoke(item: ChecklistItem, noteId : Long): Long {
        return repository.upsertItem(item, noteId)
    }
}