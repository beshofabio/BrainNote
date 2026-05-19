package com.fabio.brainnote.domain.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatToDate(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(date)
}