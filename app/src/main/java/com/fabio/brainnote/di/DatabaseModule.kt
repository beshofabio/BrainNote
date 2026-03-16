package com.fabio.brainnote.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.dao.ChecklistItemDao
import com.fabio.brainnote.data.dao.NoteDao
import com.fabio.brainnote.data.dao.NoteLinkDao
import com.fabio.brainnote.data.dao.ReminderDao
import com.fabio.brainnote.data.dao.VoiceNoteDao
import com.fabio.brainnote.data.db.BrainNoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideAppDatabase(@ApplicationContext context: Context) : BrainNoteDatabase {
        return Room.databaseBuilder(
            context,
            BrainNoteDatabase::class.java,
            "BrainNoteDB.db"
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addCallback(DatabaseCallback()).build()
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
//            Log.d("DatabaseCallback", "Database creation started")
//            val prepopulateThread = Thread {
//                try {
//                    db.execSQL("""
//                    INSERT INTO user (id, username, password, user_type)
//                    VALUES (1, 'admin', '123', 1)
//                """.trimIndent())
//                    val transactionTypes = listOf(
//                        "RECEIPT",
//                        "SHIPMENT",
//                        "ITEMS",
//                        "PHYSICALCOUNT"
//                    )
//                    transactionTypes.forEach { type ->
//                        db.execSQL("""
//                        INSERT INTO user_type (
//                            user_id, transaction_type,
//                            restrict_transaction_access
//                        ) VALUES (
//                            1, '$type',
//                            0
//                        )
//                    """.trimIndent())
//                    }
//                    Log.d("DatabaseCallback", "Prepopulated admin user and 10 user types")
//
//                } catch (e: Exception) {
//                    Log.e("DatabaseCallback", "Error during DB prepopulation", e)
//                }
//            }
//            prepopulateThread.start()
        }
    }
}