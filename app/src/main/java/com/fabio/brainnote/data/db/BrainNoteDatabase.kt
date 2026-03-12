package com.fabio.brainnote.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fabio.brainnote.data.model.CategoryEntity
import com.fabio.brainnote.data.model.ChecklistItemEntity
import com.fabio.brainnote.data.model.NoteEntity
import com.fabio.brainnote.data.model.NoteHistoryEntity
import com.fabio.brainnote.data.model.NoteLinkEntity
import com.fabio.brainnote.data.model.ReminderEntity
import com.fabio.brainnote.data.model.VoiceNoteEntity


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
    version = 1
)
abstract class BrainNoteDatabase : RoomDatabase() {

}