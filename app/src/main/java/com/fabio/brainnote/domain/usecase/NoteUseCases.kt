package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.note.GetEntireClusterUseCase
import com.fabio.brainnote.domain.usecase.note.GetNoteByIdUseCase
import com.fabio.brainnote.domain.usecase.note.GetNotesByCategoryUseCase
import com.fabio.brainnote.domain.usecase.note.SaveFullNoteUseCase
import com.fabio.brainnote.domain.usecase.note.UpsertNoteUseCase

data class NoteUseCases(
    val getNoteById: GetNoteByIdUseCase,
    val getEntireCluster: GetEntireClusterUseCase,
    val getNotesByCategory: GetNotesByCategoryUseCase,
    val upsertNote: UpsertNoteUseCase,
    val saveFullNote : SaveFullNoteUseCase
)