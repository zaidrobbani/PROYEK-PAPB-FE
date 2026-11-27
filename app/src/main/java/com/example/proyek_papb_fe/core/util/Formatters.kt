package com.example.proyek_papb_fe.core.util

import java.text.SimpleDateFormat
import java.util.Locale

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

fun rupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

fun formatShortDate(millis: Long): String = SimpleDateFormat("d MMM yyyy", IndonesianLocale).format(millis)

fun formatLongDate(millis: Long): String = SimpleDateFormat("d MMMM yyyy", IndonesianLocale).format(millis)

fun formatDateTime(millis: Long): String = SimpleDateFormat("d MMM yyyy, HH.mm", IndonesianLocale).format(millis)

fun formatTime(millis: Long): String = SimpleDateFormat("HH.mm", IndonesianLocale).format(millis)
