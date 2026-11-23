package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

enum class PaymentStatus { Menunggu, Berhasil, Gagal, Dibatalkan }

enum class FundStatus(val label: String) {
    Tertahan("Dana Ditahan"),
    Dilepas("Selesai")
}

data class UmkmTransaction(
    val code: String,
    val projectId: String,
    val projectTitle: String,
    val freelancerName: String,
    val paidAtMillis: Long,
    val amount: Long,
    val paymentStatus: PaymentStatus,
    val fundStatus: FundStatus?,
    @param:DrawableRes val iconRes: Int
)

private val WibZone: TimeZone = TimeZone.getTimeZone("UTC")
private val IndonesianLocale: Locale = Locale.forLanguageTag("id-ID")

private fun wib(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
    Calendar.getInstance(WibZone).apply {
        clear()
        set(year, month - 1, day, hour, minute)
    }.timeInMillis

fun formatTransactionTime(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, HH:mm", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

fun transactionMonthKey(millis: Long): String =
    SimpleDateFormat("yyyy-MM", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

fun formatMonthLong(millis: Long): String =
    SimpleDateFormat("MMMM yyyy", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

fun formatMonthShort(millis: Long): String =
    SimpleDateFormat("MMM yyyy", IndonesianLocale).apply { timeZone = WibZone }.format(millis)
