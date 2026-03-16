package com.fabio.brainnote.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fabio.brainnote.data.dao.CategoryDao
import com.fabio.brainnote.data.dao.ChecklistItemDao
import com.fabio.brainnote.data.dao.NoteDao
import com.fabio.brainnote.data.dao.NoteLinkDao
import com.fabio.brainnote.data.dao.ReminderDao
import com.fabio.brainnote.data.dao.VoiceNoteDao
import com.fabio.brainnote.data.model.CategoryEntity
import com.fabio.brainnote.data.model.ChecklistItemEntity
import com.fabio.brainnote.data.model.NoteEntity
import com.fabio.brainnote.data.model.NoteHistoryEntity
import com.fabio.brainnote.data.model.NoteLinkEntity
import com.fabio.brainnote.data.model.ReminderEntity
import com.fabio.brainnote.data.model.VoiceNoteEntity
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
}