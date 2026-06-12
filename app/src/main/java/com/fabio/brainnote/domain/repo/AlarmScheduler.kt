package com.fabio.brainnote.domain.repo

import com.fabio.brainnote.domain.model.Reminder

interface AlarmScheduler {
    fun schedule(reminder: Reminder, noteTitle: String, noteContent: String)
    fun cancel(reminder: Reminder)
}