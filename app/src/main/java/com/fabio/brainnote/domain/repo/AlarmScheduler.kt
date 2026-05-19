package com.fabio.brainnote.domain.repo // Or com.fabio.brainnote.domain.alarm

import com.fabio.brainnote.domain.model.Reminder

interface AlarmScheduler {
    fun schedule(reminder: Reminder, noteTitle: String, noteContent: String)
    fun cancel(reminder: Reminder)
}