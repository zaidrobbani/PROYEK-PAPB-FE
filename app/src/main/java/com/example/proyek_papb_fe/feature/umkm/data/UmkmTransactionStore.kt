package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** status_pembayaran pada Blueprint Fungsi 3. */
enum class PaymentStatus { Menunggu, Berhasil, Gagal, Dibatalkan }

/**
 * status_dana pada Blueprint Fungsi 3: TERTAHAN setelah pembayaran berhasil,
 * DILEPAS setelah hasil akhir disetujui UMKM. Label tampilan mengikuti chip Figma.
 */
enum class FundStatus(val label: String) {
    Tertahan("Dana Ditahan"),
    Dilepas("Selesai")
}

data class UmkmTransaction(
    /** Kode transaksi yang tampil di kartu, misalnya "TMK-20261024-88492". */
    val code: String,
    val projectId: String,
    val projectTitle: String,
    val freelancerName: String,
    /** Waktu pembayaran berhasil, disimpan sebagai jam WIB tanpa geser zona waktu. */
    val paidAtMillis: Long,
    /** Total pembayaran UMKM = nilai proyek (Blueprint), rupiah bulat. */
    val amount: Long,
    val paymentStatus: PaymentStatus,
    /** Null bila pembayaran belum berhasil. */
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

/** "24 Okt 2026, 14:32" seperti Figma. */
fun formatTransactionTime(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, HH:mm", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

/** Kunci bulan untuk pengelompokan, misalnya "2026-10". */
fun transactionMonthKey(millis: Long): String =
    SimpleDateFormat("yyyy-MM", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

/** "Oktober 2026" untuk judul kelompok. */
fun formatMonthLong(millis: Long): String =
    SimpleDateFormat("MMMM yyyy", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

/** "Okt 2026" untuk tombol periode. */
fun formatMonthShort(millis: Long): String =
    SimpleDateFormat("MMM yyyy", IndonesianLocale).apply { timeZone = WibZone }.format(millis)

/**
 * DATA DUMMY riwayat pembayaran UMKM dari frame Figma Riwayat Transaksi, dikaitkan dengan
 * project dummy di Project Saya. Bukan transaksi sungguhan; tidak ada uang yang berpindah.
 */
object UmkmTransactionStore {
    val transactions: List<UmkmTransaction> = listOf(
        UmkmTransaction(
            code = "TMK-20261024-88492",
            projectId = "dummy-project-kopi",
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi",
            freelancerName = "Andi Ramadhan",
            paidAtMillis = wib(2026, 10, 24, 14, 32),
            amount = 1_500_000,
            paymentStatus = PaymentStatus.Berhasil,
            fundStatus = FundStatus.Tertahan,
            iconRes = R.drawable.ic_transaction_escrow
        ),
        UmkmTransaction(
            code = "TMK-20261018-77102",
            projectId = "dummy-project-katalog",
            projectTitle = "Foto Katalog Menu Baru Warung Kopi Senja",
            freelancerName = "Maya Safitri",
            paidAtMillis = wib(2026, 10, 18, 9, 15),
            amount = 900_000,
            paymentStatus = PaymentStatus.Berhasil,
            fundStatus = FundStatus.Tertahan,
            iconRes = R.drawable.ic_transaction_qris
        ),
        UmkmTransaction(
            code = "TMK-20261012-65411",
            projectId = "dummy-project-stiker",
            projectTitle = "Desain Stiker Label Toples Sambal Roa 150ml",
            freelancerName = "Budi Santoso",
            paidAtMillis = wib(2026, 10, 12, 16, 40),
            amount = 450_000,
            paymentStatus = PaymentStatus.Berhasil,
            fundStatus = FundStatus.Dilepas,
            iconRes = R.drawable.ic_transaction_done
        ),
        UmkmTransaction(
            code = "TMK-20260928-54129",
            projectId = "dummy-project-instagram",
            projectTitle = "Setup Akun Instagram Bisnis & 9 Feed",
            freelancerName = "Nadia Putri",
            paidAtMillis = wib(2026, 9, 28, 11, 20),
            amount = 2_050_000,
            paymentStatus = PaymentStatus.Berhasil,
            fundStatus = FundStatus.Dilepas,
            iconRes = R.drawable.ic_transaction_campaign
        )
    )
}
