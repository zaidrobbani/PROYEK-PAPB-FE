package com.example.proyek_papb_fe.core.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateTimeParser {
    private val utcPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    fun parseUtcMillis(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        for (pattern in utcPatterns) {
            val format = SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }
            try {
                return format.parse(value)?.time
            } catch (_: ParseException) {
            }
        }
        return null
    }

    fun parseDateMillis(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(value)?.time
        } catch (_: ParseException) {
            null
        }
    }

    fun formatDate(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(millis)

    fun parseDateUtcMillis(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(value)?.time
        } catch (_: ParseException) {
            null
        }
    }

    fun formatDateUtc(utcMillis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(utcMillis)
}
