package com.example.proyek_papb_fe.feature.freelancer.submission.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionStatusScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionStatusUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

/** Lama simulasi peninjauan oleh pemilik UMKM sebelum penawaran disetujui. */
private const val SimulatedReviewMillis = 3_000L

/** 1500000 → "Rp 1.500.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** Waktu kirim dalam WIB, mis. "24 Okt 2026, 14.05 WIB". */
private fun formatSubmittedAt(millis: Long): String {
    val format = SimpleDateFormat("d MMM yyyy, HH.mm", IndonesianLocale).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }
    return "${format.format(Date(millis))} WIB"
}

/** Label waktu relatif di kartu, mis. "Baru saja" atau "5 menit lalu". */
private fun formatSubmittedAgo(millis: Long, now: Long = System.currentTimeMillis()): String {
    val elapsed = (now - millis).coerceAtLeast(0)
    return when {
        elapsed < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
        elapsed < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(elapsed)} menit lalu"
        elapsed < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(elapsed)} jam lalu"
        else -> "${TimeUnit.MILLISECONDS.toDays(elapsed)} hari lalu"
    }
}

/**
 * Container "Status Pengajuan": lookup project + penawaran terkirim (read-only),
 * lalu meneruskan aksi ke navigasi.
 */
@Composable
fun SubmissionStatusContainer(
    projectId: Int,
    onBack: () -> Unit,
    onOpenNegotiation: (projectId: Int) -> Unit,
    onFindOtherProjects: () -> Unit,
    onViewProjectDetail: (projectId: Int) -> Unit
) {
    val project = remember(projectId) { FreelancerProjectDummyData.getById(projectId) }
    // Dibaca langsung (snapshot state): saat project dibatalkan dari Negosiasi, layar ini
    // (masih di back stack) ikut ter-update ketika pengguna kembali.
    val offer: SubmittedOffer? = SubmittedOfferStore.get(projectId)

    if (project == null || offer == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Status Pengajuan", onBack = onBack)
            FeaturePlaceholder(
                title = "Pengajuan tidak ditemukan",
                description = "Belum ada penawaran terkirim untuk project dengan id $projectId."
            )
        }
        return
    }

    // Satu sumber state untuk tahap + banner. Nilai awal dari penawaran ini: penawaran
    // yang baru dikirim selalu false (masih ditinjau UMKM).
    var isApproved by rememberSaveable(projectId) { mutableStateOf(offer.isApproved) }

    // Simulasi persetujuan UMKM (belum ada backend): ±3 detik setelah layar dibuka.
    // Coroutine otomatis batal jika layar ditinggalkan sebelum waktunya; tidak berjalan
    // lagi bila penawaran sudah disetujui.
    LaunchedEffect(projectId) {
        if (!isApproved && !offer.isCancelled) {
            delay(SimulatedReviewMillis)
            SubmittedOfferStore.markApproved(projectId)
            isApproved = true
        }
    }

    val cancellationReason = offer.cancellation?.reason?.label

    val state = remember(project, offer, isApproved, cancellationReason) {
        SubmissionStatusUiState(
            submittedAgo = formatSubmittedAgo(offer.submittedAtMillis),
            projectTitle = project.title,
            clientLine = "${project.clientName}, ${project.ownerName}",
            clientName = project.clientName,
            offerAmount = formatRupiah(offer.offerAmount),
            duration = "${offer.durationDays} hari",
            otherApplicants = "${project.otherApplicantCount} freelancer",
            submittedAtLabel = formatSubmittedAt(offer.submittedAtMillis),
            isApproved = isApproved,
            cancellationReason = cancellationReason,
            isAgreed = offer.isAgreed,
            isCompleted = offer.isCompleted
        )
    }

    SubmissionStatusScreen(
        state = state,
        onBack = onBack,
        onOpenNegotiationClick = { onOpenNegotiation(projectId) },
        onFindOtherProjectsClick = onFindOtherProjects,
        onViewDetailClick = { onViewProjectDetail(projectId) }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SubmissionStatusContainerPreview() {
    SubmittedOfferStore.submit(
        SubmittedOffer(
            projectId = 101,
            offerAmount = 1_500_000,
            platformFee = 75_000,
            netAmount = 1_425_000,
            durationDays = 14,
            message = "Contoh pesan proposal.",
            submittedAtMillis = System.currentTimeMillis()
        )
    )
    SubmissionStatusContainer(
        projectId = 101,
        onBack = {},
        onOpenNegotiation = {},
        onFindOtherProjects = {},
        onViewProjectDetail = {}
    )
}
