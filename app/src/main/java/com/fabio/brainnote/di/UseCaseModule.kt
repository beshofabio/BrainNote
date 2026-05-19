package com.fabio.brainnote.di

import com.fabio.brainnote.di.qualifiers.IoDispatcher
import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import com.fabio.brainnote.domain.repo.CategoryRepository
import com.fabio.brainnote.domain.repo.ChecklistRepository
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import com.fabio.brainnote.domain.repo.VoiceNoteRepository
import com.fabio.brainnote.domain.usecase.CategoryUseCases
import com.fabio.brainnote.domain.usecase.MediaUseCases
import com.fabio.brainnote.domain.usecase.NoteUseCases
import com.fabio.brainnote.domain.usecase.category.GetAllCategoriesUseCase
import com.fabio.brainnote.domain.usecase.media.*
import com.fabio.brainnote.domain.usecase.note.GetEntireClusterUseCase
import com.fabio.brainnote.domain.usecase.note.GetNoteByIdUseCase
import com.fabio.brainnote.domain.usecase.note.GetNotesByCategoryUseCase
import com.fabio.brainnote.domain.usecase.note.SaveFullNoteUseCase
import com.fabio.brainnote.domain.usecase.note.UpsertNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UseCaseModule {
    @Singleton
    @Provides
    fun provideNoteUseCases(
        noteRepository: NoteRepository,
        checklistRepository: ChecklistRepository,
        voiceNoteRepository: VoiceNoteRepository,
        reminderRepository: ReminderRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): NoteUseCases {
        return NoteUseCases(
            getEntireCluster = GetEntireClusterUseCase(noteRepository, ioDispatcher),
            upsertNote = UpsertNoteUseCase(noteRepository, ioDispatcher),
            getNoteById = GetNoteByIdUseCase(noteRepository, ioDispatcher),
            getNotesByCategory = GetNotesByCategoryUseCase(noteRepository, ioDispatcher),
            saveFullNote = SaveFullNoteUseCase(noteRepository, checklistRepository, voiceNoteRepository, reminderRepository)
        )
    }

    @Singleton
    @Provides
    fun provideCategoryUseCases(
        repository: CategoryRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CategoryUseCases {
        return CategoryUseCases(
            getAll = GetAllCategoriesUseCase(repository, ioDispatcher)
        )
    }

    @Singleton
    @Provides
    fun provideMediaUseCases(
        audioRecorderRepository: AudioRecorderRepository,
        audioPlayerRepository: AudioPlayerRepository,
        imageStorageManager: ImageStorageRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MediaUseCases {
        return MediaUseCases(
            saveImage = SaveImageUseCase(
                repository = imageStorageManager,
                ioDispatcher = ioDispatcher
            ),
            startAudio = StartAudioUseCase(
                repository = audioRecorderRepository
            ),
            stopAudio = StopAudioUseCase(audioRecorderRepository),
            deleteAudio = DeleteAudioUseCase(audioRecorderRepository),
            playAudio = PlayAudioUseCase(audioPlayerRepository),
            pauseAudio = PauseAudioUseCase(audioPlayerRepository),
            resumeAudio = ResumeAudioUseCase(audioPlayerRepository),
            seekAudio = SeekAudioUseCase(audioPlayerRepository),
            getPlaybackProgress = GetPlaybackProgressUseCase(audioPlayerRepository)
        )
    }
}
