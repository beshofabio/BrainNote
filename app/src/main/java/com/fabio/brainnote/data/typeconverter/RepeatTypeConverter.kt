package com.fabio.brainnote.data.typeconverter

import androidx.room.TypeConverter
import com.fabio.brainnote.domain.RepeatType

class RepeatTypeConverter {
    @TypeConverter
    fun fromRepeatType(value: RepeatType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRepeatType(value: String?): RepeatType? {
        return value?.let { RepeatType.valueOf(it) }
    }
}