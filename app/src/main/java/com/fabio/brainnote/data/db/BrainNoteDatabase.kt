package com.fabio.brainnote.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fabio.brainnote.data.dao.*
import com.fabio.brainnote.data.model.*
import com.fabio.brainnote.data.typeconverter.RepeatTypeConverter


@Database(
    entities = [
        NoteEntity::class,
        CategoryEntity::class,
        ChecklistItemEntity::class,
        ReminderEntity::class,
        VoiceNoteEntity::class,
        NoteHistoryEntity::class,
        NoteLinkEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RepeatTypeConverter::class)
abstract class BrainNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun reminderDao(): ReminderDao
    abstract fun voiceNoteDao(): VoiceNoteDao
    abstract fun noteLinkDao(): NoteLinkDao
    abstract fun noteHistoryDao(): NoteHistoryDao
}
