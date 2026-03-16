package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.note.GetEntireClusterUseCase
import com.fabio.brainnote.domain.usecase.note.UpsertNoteUseCase

data class NoteUseCases(
    val getEntireCluster: GetEntireClusterUseCase,
    val upsertNote: UpsertNoteUseCase
)