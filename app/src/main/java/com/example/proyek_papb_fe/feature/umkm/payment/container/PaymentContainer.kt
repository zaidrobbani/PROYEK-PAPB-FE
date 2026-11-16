package com.example.proyek_papb_fe.feature.umkm.payment.container

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.model.PaymentStatus
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentScreen
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentUiState
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToInt

/** Lama simulasi proses pembayaran setelah "Bayar (simulasi)" ditekan (1–2 detik). */
private const val SimulatedProcessingMillis = 1_500L

/** "Desainer Grafis & Identitas Kemasan" → "Desainer Grafis" (Figma menampilkan peran singkat). */
internal fun shortRole(headline: String): String = headline.substringBefore(" & ").trim()

/**
 * Container Pembayaran (Figma 17:1267) untuk [projectId]. Pembayaran dibuat saat UMKM menyetujui
 * rencana ([PaymentStore.createForApprovedPlan]); layar ini hanya membaca pembayaran itu.
 * "Bayar (simulasi)": READY → PROCESSING (tap ganda & Back diabaikan) → ±1,5 detik → PAID →
 * [onPaid] membuka Bukti Pembayaran. Pembayaran yang sudah PAID langsung diteruskan ke bukti,
 * sehingga tidak bisa dibayar ulang.
 */
@Composable
fun PaymentContainer(
    projectId: Int,
    onBack: () -> Unit,
    onPaid: (paymentId: String) -> Unit
) {
    val payment = PaymentStore.forProject(projectId)
    val project = FreelancerProjectDummyData.getById(projectId)
    val offer = SubmittedOfferStore.get(projectId)

    if (payment == null || project == null || offer == null) {
        // Belum ada pembayaran: buat bila rencana sudah disetujui, selain itu kembali.
        LaunchedEffect(projectId) {
            if (PaymentStore.createForApprovedPlan(projectId) == null) onBack()
        }
        return
    }
    if (payment.status == PaymentStatus.PAID) {
        LaunchedEffect(payment.paymentId) { onPaid(payment.paymentId) }
        return
    }

    val isProcessing = payment.status == PaymentStatus.PROCESSING
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(SimulatedProcessingMillis)
            PaymentStore.markPaid(projectId)
        }
    }
    // Bila layar ditutup paksa saat proses (mis. pindah tab), pembayaran kembali siap dibayar.
    DisposableEffect(projectId) {
        onDispose { PaymentStore.cancelProcessing(projectId) }
    }
    // Back diabaikan selama proses agar pembayaran tidak terputus di tengah jalan.
    BackHandler(enabled = isProcessing) {}

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }
    val remainingSeconds = ((payment.createdAtMillis + PaymentStore.QrisValidityMillis - now) / 1000).coerceAtLeast(0)
    val profile = FreelancerProfileStore.profile
    val feePercent = if (payment.amount > 0) (payment.platformFee * 100.0 / payment.amount).roundToInt() else 0

    PaymentScreen(
        state = PaymentUiState(
            projectTitle = project.title,
            freelancerName = profile.fullName,
            freelancerPhotoUri = profile.photoUri,
            freelancerRating = String.format(Locale.US, "%.1f", profile.rating),
            freelancerRole = shortRole(profile.headline),
            duration = "${offer.durationDays} Hari Kalender",
            countdown = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
            total = formatRupiah(payment.amount),
            agreedValue = formatRupiah(payment.amount),
            platformFeeLabel = "Biaya Platform TEMUKARYA ($feePercent%)",
            platformFee = formatRupiah(payment.platformFee),
            freelancerNet = formatRupiah(payment.freelancerNet),
            isProcessing = isProcessing
        ),
        onBack = { if (!isProcessing) onBack() },
        onPay = { PaymentStore.startProcessing(projectId) }
    )
}
