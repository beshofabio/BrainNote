package com.fabio.brainnote.domain.usecase.checklistitem

import com.fabio.brainnote.domain.repo.ChecklistRepository
import javax.inject.Inject

class DeleteChecklistItemUseCase @Inject constructor(
    private val repository: ChecklistRepository
) {

    suspend operator fun invoke(id: Long) {
        repository.deleteItem(id)
    }
}