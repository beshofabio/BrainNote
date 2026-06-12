package com.fabio.brainnote.domain.usecase

import com.fabio.brainnote.domain.usecase.note.DeleteNoteUseCase
import com.fabio.brainnote.domain.usecase.note.GetEntireClusterUseCase
import com.fabio.brainnote.domain.usecase.note.GetNoteByIdUseCase
import com.fabio.brainnote.domain.usecase.note.GetNoteHistoryUseCase
import com.fabio.brainnote.domain.usecase.note.GetNotesByCategoryUseCase
import com.fabio.brainnote.domain.usecase.note.SaveFullNoteUseCase
import com.fabio.brainnote.domain.usecase.note.UpsertNoteUseCase
import javax.inject.Inject

class NoteUseCases @Inject constructor(
    val getNoteById: GetNoteByIdUseCase,
    val getEntireCluster: GetEntireClusterUseCase,
    val getNotesByCategory: GetNotesByCategoryUseCase,
    val upsertNote: UpsertNoteUseCase,
    val saveFullNote : SaveFullNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val getNoteHistory: GetNoteHistoryUseCase
)
