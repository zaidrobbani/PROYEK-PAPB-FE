package com.example.proyek_papb_fe.feature.umkm.data

import com.example.proyek_papb_fe.core.domain.model.CancelReasonCode
import com.example.proyek_papb_fe.core.domain.model.PaymentStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import java.time.Instant
import java.time.ZoneId

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDateTime

data class UmkmNegotiation(
    val conversationId: String,
    val status: ProjectStatus,
    val isPaid: Boolean,
    val agreedValue: Long?,
    val duration: String,
    val output: String,
    val paymentMethod: String,
    val umkmAgreed: Boolean,
    val freelancerAgreed: Boolean,
    val cancellationReason: UmkmCancellationReason? = null,
    val cancelledAt: LocalDateTime? = null
)

enum class UmkmCancellationReason(val label: String) {
    HargaBelumSesuai("Harga belum sesuai"),
    JadwalTidakCocok("Jadwal pengerjaan tidak cocok"),
    KebutuhanBerubah("Kebutuhan project berubah"),
    Lainnya("Alasan lainnya")
}

enum class ContinueProjectResult {
    WaitingFreelancer,
    BothAgreed,
    IncompleteAgreement,
    NotAllowed
}

fun applyUmkmContinue(current: UmkmNegotiation): Pair<UmkmNegotiation, ContinueProjectResult> {
    if (current.isPaid || current.umkmAgreed || current.status != ProjectStatus.MenungguKesepakatan) {
        return current to ContinueProjectResult.NotAllowed
    }
    if (current.agreedValue == null) return current to ContinueProjectResult.IncompleteAgreement
    val both = current.freelancerAgreed
    val updated = current.copy(
        umkmAgreed = true,
        status = if (both) ProjectStatus.MenungguPembayaran else ProjectStatus.MenungguKesepakatan
    )
    return updated to if (both) ContinueProjectResult.BothAgreed else ContinueProjectResult.WaitingFreelancer
}

fun applyUmkmCancel(
    current: UmkmNegotiation,
    reason: UmkmCancellationReason,
    now: LocalDateTime = LocalDateTime.now()
): UmkmNegotiation? {
    val cancellable = current.status == ProjectStatus.MenungguKesepakatan ||
        current.status == ProjectStatus.MenungguPembayaran
    if (current.isPaid || !cancellable) return null
    return current.copy(status = ProjectStatus.Dibatalkan, cancellationReason = reason, cancelledAt = now)
}

/** Cache kesepakatan per percakapan, dipetakan dari detail alur BE. */
object UmkmNegotiationStore {
    private val items = mutableStateListOf<UmkmNegotiation>()

    fun negotiation(conversationId: String): UmkmNegotiation? = items.firstOrNull { it.conversationId == conversationId }

    fun upsert(conversationId: String, detail: WorkflowDetail) {
        val agreement = detail.agreement
        val negotiation = UmkmNegotiation(
            conversationId = conversationId,
            status = projectStatusOf(detail.project.status),
            isPaid = detail.payment?.status == PaymentStatusCode.BERHASIL,
            agreedValue = agreement?.agreedPrice,
            duration = agreement?.let { "${it.durationDays} hari kalender" }.orEmpty(),
            output = agreement?.scopeOfWork.orEmpty(),
            paymentMethod = agreement?.paymentMethodLabel ?: "QRIS (simulasi)",
            umkmAgreed = agreement?.umkmApprovedAtMillis != null,
            freelancerAgreed = agreement?.freelancerApprovedAtMillis != null,
            cancellationReason = when (detail.cancellation?.reason) {
                CancelReasonCode.HARGA_TIDAK_SESUAI -> UmkmCancellationReason.HargaBelumSesuai
                CancelReasonCode.JADWAL_TIDAK_COCOK -> UmkmCancellationReason.JadwalTidakCocok
                CancelReasonCode.KEBUTUHAN_BERUBAH -> UmkmCancellationReason.KebutuhanBerubah
                null -> null
                else -> UmkmCancellationReason.Lainnya
            },
            cancelledAt = detail.cancellation?.cancelledAtMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
        )
        val index = items.indexOfFirst { it.conversationId == conversationId }
        if (index >= 0) items[index] = negotiation else items.add(negotiation)
    }

    fun reset() {
        items.clear()
    }
}

fun UmkmCancellationReason.toCode(): String = when (this) {
    UmkmCancellationReason.HargaBelumSesuai -> CancelReasonCode.HARGA_TIDAK_SESUAI
    UmkmCancellationReason.JadwalTidakCocok -> CancelReasonCode.JADWAL_TIDAK_COCOK
    UmkmCancellationReason.KebutuhanBerubah -> CancelReasonCode.KEBUTUHAN_BERUBAH
    UmkmCancellationReason.Lainnya -> CancelReasonCode.LAINNYA
}
