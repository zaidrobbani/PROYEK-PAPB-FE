package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.PaymentMethod
import com.example.proyek_papb_fe.core.data.model.PaymentStatus
import com.example.proyek_papb_fe.core.data.model.ProjectPayment
import com.example.proyek_papb_fe.core.data.model.WorkPlanStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Pembayaran UMKM di memori, satu per project (simulasi; tidak ada payment gateway).
 * Berbasis snapshot state. Siklus: [createForApprovedPlan] (READY) → [startProcessing]
 * (PROCESSING) → [markPaid] (PAID). Saat PAID, project resmi berjalan lewat transisi yang
 * sudah ada, [SubmittedOfferStore.markAgreed] (Negosiasi → Project Berjalan).
 */
object PaymentStore {

    /** Lama berlaku kode QRIS (Figma: "Selesaikan dalam 14:52 menit"). */
    const val QrisValidityMillis: Long = 15 * 60 * 1000L

    private val payments = mutableStateMapOf<Int, ProjectPayment>()

    /** Nomor urut transaksi; Figma memakai contoh "...-88492". */
    private var nextSequence = 88492

    fun forProject(projectId: Int): ProjectPayment? = payments[projectId]

    fun byId(paymentId: String): ProjectPayment? = payments.values.firstOrNull { it.paymentId == paymentId }

    /**
     * Buat pembayaran untuk rencana yang sudah disetujui UMKM. Idempoten: bila project ini
     * sudah punya pembayaran (termasuk yang sudah PAID), pembayaran itu yang dikembalikan,
     * sehingga tidak pernah ada pembayaran ganda. null bila syarat belum terpenuhi.
     */
    fun createForApprovedPlan(projectId: Int, nowMillis: Long = System.currentTimeMillis()): ProjectPayment? {
        payments[projectId]?.let { return it }
        val offer = SubmittedOfferStore.get(projectId) ?: return null
        if (offer.isCancelled || !offer.isApproved || offer.isAgreed) return null
        if (WorkPlanStore.get(projectId)?.status != WorkPlanStatus.APPROVED) return null
        val payment = ProjectPayment(
            paymentId = "TMK-${idDate(nowMillis)}-${nextSequence++}",
            projectId = projectId,
            amount = offer.offerAmount,
            platformFee = offer.platformFee,
            freelancerNet = offer.netAmount,
            method = PaymentMethod.QRIS,
            status = PaymentStatus.READY,
            createdAtMillis = nowMillis
        )
        payments[projectId] = payment
        return payment
    }

    /** READY → PROCESSING. false bila sudah diproses/dibayar (tap ganda diabaikan). */
    fun startProcessing(projectId: Int): Boolean {
        val payment = payments[projectId] ?: return false
        if (payment.status != PaymentStatus.READY) return false
        payments[projectId] = payment.copy(status = PaymentStatus.PROCESSING)
        return true
    }

    /** PROCESSING → PAID, lalu project resmi berjalan (transisi yang sudah ada). */
    fun markPaid(projectId: Int, paidAtMillis: Long = System.currentTimeMillis()): ProjectPayment? {
        val payment = payments[projectId] ?: return null
        if (payment.status == PaymentStatus.PAID) return payment
        if (payment.status != PaymentStatus.PROCESSING) return null
        val paid = payment.copy(status = PaymentStatus.PAID, paidAtMillis = paidAtMillis)
        payments[projectId] = paid
        SubmittedOfferStore.markAgreed(projectId, agreedAtMillis = paidAtMillis)
        return paid
    }

    /** Proses dibatalkan sebelum selesai (mis. layar ditutup paksa): kembali siap dibayar. */
    fun cancelProcessing(projectId: Int) {
        val payment = payments[projectId] ?: return
        if (payment.status == PaymentStatus.PROCESSING) payments[projectId] = payment.copy(status = PaymentStatus.READY)
    }

    private fun idDate(millis: Long): String =
        SimpleDateFormat("yyyyMMdd", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("Asia/Jakarta") }.format(Date(millis))
}
