package com.example.proyek_papb_fe.core.util

import java.text.SimpleDateFormat
import java.util.Locale

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

/** `1500000` → `Rp 1.500.000`. */
fun rupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** Tanggal lokal (WIB perangkat), mis. `12 Nov 2026`. */
fun formatShortDate(millis: Long): String = SimpleDateFormat("d MMM yyyy", IndonesianLocale).format(millis)

/** Mis. `12 November 2026`. */
fun formatLongDate(millis: Long): String = SimpleDateFormat("d MMMM yyyy", IndonesianLocale).format(millis)

/** Mis. `12 Nov 2026, 14.30`. */
fun formatDateTime(millis: Long): String = SimpleDateFormat("d MMM yyyy, HH.mm", IndonesianLocale).format(millis)

/** Mis. `14.30`. */
fun formatTime(millis: Long): String = SimpleDateFormat("HH.mm", IndonesianLocale).format(millis)
