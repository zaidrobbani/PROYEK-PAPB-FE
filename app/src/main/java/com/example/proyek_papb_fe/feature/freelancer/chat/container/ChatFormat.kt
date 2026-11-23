package com.example.proyek_papb_fe.feature.freelancer.chat.container

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val IndonesianLocale = Locale.forLanguageTag("id-ID")
private val Jakarta = TimeZone.getTimeZone("Asia/Jakarta")

private fun format(pattern: String, millis: Long): String =
    SimpleDateFormat(pattern, IndonesianLocale).apply { timeZone = Jakarta }.format(Date(millis))

private fun dayIndex(millis: Long): Int =
    Calendar.getInstance(Jakarta).apply { timeInMillis = millis }.let { it.get(Calendar.YEAR) * 1000 + it.get(Calendar.DAY_OF_YEAR) }

internal fun isSameDay(a: Long, b: Long): Boolean = dayIndex(a) == dayIndex(b)

internal fun formatListTime(millis: Long, now: Long = System.currentTimeMillis()): String {
    val yesterday = Calendar.getInstance(Jakarta).apply { timeInMillis = now; add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
    return when {
        isSameDay(millis, now) -> format("HH.mm", millis)
        isSameDay(millis, yesterday) -> "Kemarin"
        else -> format("d MMM", millis)
    }
}

internal fun formatBubbleTime(millis: Long): String = "${format("HH.mm", millis)} WIB"

internal fun formatDateSeparator(millis: Long): String = format("EEEE, d MMM yyyy", millis)

internal fun formatShortDate(millis: Long): String = format("d MMM yyyy", millis)
