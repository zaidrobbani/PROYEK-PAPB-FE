package com.example.proyek_papb_fe.core.data.model

/**
 * Status pembayaran UMKM (berbeda dari [ProjectStatus]):
 * READY = siap dibayar (setelah rencana disetujui), PROCESSING = sedang diproses (simulasi
 * ±1,5 detik), PAID = berhasil; dana ditahan TEMUKARYA sampai UMKM menyetujui hasil.
 */
enum class PaymentStatus { READY, PROCESSING, PAID }

/** Metode bayar. Figma hanya mendefinisikan QRIS (simulasi). */
enum class PaymentMethod(val label: String) { QRIS("QRIS") }

/**
 * Pembayaran project oleh UMKM (Figma 17:1267 → 17:3177). Nilai disalin dari penawaran
 * yang disepakati ([SubmittedOffer]) saat pembayaran dibuat, sehingga layar Pembayaran dan
 * Bukti Pembayaran selalu menampilkan angka yang sama.
 */
data class ProjectPayment(
    /** Nomor transaksi, mis. "TMK-20261024-88492". */
    val paymentId: String,
    val projectId: Int,
    /** Nilai project disepakati = total yang ditransfer UMKM. */
    val amount: Long,
    /** Biaya platform TEMUKARYA (5%), dipotong dari bagian freelancer. */
    val platformFee: Long,
    /** Diterima freelancer (net) = [amount] − [platformFee]. */
    val freelancerNet: Long,
    val method: PaymentMethod,
    val status: PaymentStatus,
    /** Pembayaran dibuat (UMKM menyetujui rencana); awal hitung mundur QRIS. */
    val createdAtMillis: Long,
    /** Waktu transaksi berhasil; null sebelum PAID. */
    val paidAtMillis: Long? = null
)
