package com.example.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatDateString(dateFormat: String): String {
    val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    serverFormat.timeZone = TimeZone.getTimeZone("UTC")

    val date: Date = serverFormat.parse(dateFormat) ?: return dateFormat

    val targetFormat = SimpleDateFormat("EEEE, d MMMM yyyy · hh.mm a", Locale("id", "ID"))
    targetFormat.timeZone = TimeZone.getDefault()

    return targetFormat.format(date)
}