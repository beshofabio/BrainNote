package com.fabio.brainnote.data.repository

import com.fabio.brainnote.data.dao.ReminderDao
import com.fabio.brainnote.data.mapper.toDomain
import com.fabio.brainnote.data.mapper.toEntity
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.repo.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val dao: ReminderDao
) : ReminderRepository {

    override suspend fun upsertReminder(reminder: Reminder, noteId : Long): Long {
        return dao.upsertReminder(reminder.toEntity(noteId))
    }

    override suspend fun deleteReminder(id: Long) {
        dao.deleteReminder(id)
    }
}