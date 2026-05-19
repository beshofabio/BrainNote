package com.fabio.brainnote.di

import android.content.Context
import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.dao.ChecklistItemDao
import com.fabio.brainnote.data.dao.NoteDao
import com.fabio.brainnote.data.dao.ReminderDao
import com.fabio.brainnote.data.dao.VoiceNoteDao
import com.fabio.brainnote.data.repository.AudioPlayerRepositoryImpl
import com.fabio.brainnote.data.repository.CategoryRepositoryImpl
import com.fabio.brainnote.data.repository.ChecklistRepositoryImpl
import com.fabio.brainnote.data.repository.NoteRepositoryImpl
import com.fabio.brainnote.data.repository.ReminderRepositoryImpl
import com.fabio.brainnote.data.repository.VoiceNoteRepositoryImpl
import com.fabio.brainnote.domain.repo.AudioPlayerRepository
import com.fabio.brainnote.domain.repo.CategoryRepository
import com.fabio.brainnote.domain.repo.ChecklistRepository
import com.fabio.brainnote.domain.repo.NoteRepository
import com.fabio.brainnote.domain.repo.ReminderRepository
import com.fabio.brainnote.domain.repo.VoiceNoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Singleton
    @Provides
    fun provideCategoryRepository(categoryDao : CategoryDao) : CategoryRepository = CategoryRepositoryImpl(dao = categoryDao)


    @Singleton
    @Provides
    fun provideChecklistRepository(checklistDao: ChecklistItemDao) : ChecklistRepository = ChecklistRepositoryImpl(dao = checklistDao)

    @Singleton
    @Provides
    fun provideNoteRepository(noteDao: NoteDao) : NoteRepository = NoteRepositoryImpl(noteDao = noteDao)

    @Singleton
    @Provides
    fun provideReminderRepsotory(reminderDao: ReminderDao) : ReminderRepository = ReminderRepositoryImpl(dao = reminderDao )

    @Singleton
    @Provides
    fun provideVoiceNoteRepository(voiceNoteDao: VoiceNoteDao) : VoiceNoteRepository = VoiceNoteRepositoryImpl(dao = voiceNoteDao)

    @Singleton
    @Provides
    fun provideAudioPlayerRepository(@ApplicationContext context: Context): AudioPlayerRepository = AudioPlayerRepositoryImpl(context)
}
