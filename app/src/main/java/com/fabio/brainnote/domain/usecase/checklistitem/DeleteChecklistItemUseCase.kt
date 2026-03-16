package com.fabio.brainnote.domain.usecase.checklistitem

import com.fabio.brainnote.domain.repo.ChecklistRepository

class DeleteChecklistItemUseCase(
    private val repository: ChecklistRepository
) {

    suspend operator fun invoke(id: Long) {
        repository.deleteItem(id)
    }
}