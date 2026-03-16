package com.fabio.brainnote.domain.model

import com.fabio.brainnote.domain.RepeatType

data class Reminder(
    val id: Long = 0,
    val triggerTime: Long,
    val repeatType: RepeatType?
)