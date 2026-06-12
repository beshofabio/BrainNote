package com.fabio.brainnote.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.dao.ChecklistItemDao
import com.fabio.brainnote.data.dao.NoteDao
import com.fabio.brainnote.data.dao.NoteHistoryDao
import com.fabio.brainnote.data.dao.NoteLinkDao
import com.fabio.brainnote.data.dao.ReminderDao
import com.fabio.brainnote.data.dao.VoiceNoteDao
import com.fabio.brainnote.data.db.BrainNoteDatabase
import com.fabio.brainnote.data.model.CategoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideNoteDao(appDatabase: BrainNoteDatabase) : NoteDao = appDatabase.noteDao()

    @Singleton
    @Provides
    fun provideCategoryDao(appDatabase: BrainNoteDatabase) : CategoryDao = appDatabase.categoryDao()

    @Singleton
    @Provides
    fun provideCheckListItemDao(appDatabase: BrainNoteDatabase) : ChecklistItemDao = appDatabase.checklistItemDao()

    @Singleton
    @Provides
    fun provideNoteLinkDao(appDatabase: BrainNoteDatabase) : NoteLinkDao = appDatabase.noteLinkDao()

    @Singleton
    @Provides
    fun provideReminderDao(appDatabase: BrainNoteDatabase) : ReminderDao = appDatabase.reminderDao()

    @Singleton
    @Provides
    fun provideVoiceNoteDao(appDatabase: BrainNoteDatabase) : VoiceNoteDao = appDatabase.voiceNoteDao()

    @Singleton
    @Provides
    fun provideNoteHistoryDao(appDatabase: BrainNoteDatabase) : NoteHistoryDao = appDatabase.noteHistoryDao()

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>

    ) : BrainNoteDatabase {
        return Room.databaseBuilder(
            context,
            BrainNoteDatabase::class.java,
            "BrainNoteDB.db"
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addCallback(DatabaseCallback(categoryDaoProvider)).build()
    }

    private class DatabaseCallback(
        private val categoryDaoProvider: Provider<CategoryDao>
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val defaultCategories = listOf(
                    CategoryEntity(id = 1, name = "Work", color = 0xFF4285F4),
                    CategoryEntity(id = 2, name = "Ideas", color = 0xFFFBBC05),
                    CategoryEntity(id = 3, name = "Plans", color = 0xFF34A853),
                    CategoryEntity(id = 4, name = "Birthday", color = 0xFFEA4335),
                    CategoryEntity(id = 5,name = "Others", color = 0xFF70757A)
                )

                categoryDaoProvider.get().upsertCategory(defaultCategories)
            }
        }
    }
}