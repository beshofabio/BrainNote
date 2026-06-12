package com.fabio.brainnote.di

import com.fabio.brainnote.data.repository.AudioPlayerRepositoryImpl
import com.fabio.brainnote.data.repository.CategoryRepositoryImpl
import com.fabio.brainnote.data.repository.ChecklistRepositoryImpl
import com.fabio.brainnote.data.repository.NoteHistoryRepositoryImpl
import com.fabio.brainnote.data.repository.NoteRepositoryImpl
import com.fabio.brainnote.data.repository.ReminderRepositoryImpl
import com.fabio.brainnote.data.repository.VoiceNoteRepositoryImpl
import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import com.fabio.brainnote.domain.repo.CategoryRepository
import com.fabio.brainnote.domain.repo.ChecklistRepository
import com.fabio.brainnote.domain.repo.NoteHistoryRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import com.fabio.brainnote.domain.repo.VoiceNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindChecklistRepository(impl: ChecklistRepositoryImpl): ChecklistRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindVoiceNoteRepository(impl: VoiceNoteRepositoryImpl): VoiceNoteRepository

    @Binds
    @Singleton
    abstract fun bindAudioPlayerRepository(impl: AudioPlayerRepositoryImpl): AudioPlayerRepository

    @Binds
    @Singleton
    abstract fun bindNoteHistoryRepository(impl: NoteHistoryRepositoryImpl): NoteHistoryRepository
}
