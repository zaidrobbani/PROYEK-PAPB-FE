package com.example.proyek_papb_fe.feature.umkm.review.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.data.ResultReviewStore
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.review.component.ProjectCompletedScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.ProjectCompletedUiState
import com.example.proyek_papb_fe.feature.umkm.review.component.formatFileSize

/**
 * Container Proyek Selesai (Figma 17:6338) untuk [projectId]. Hanya untuk project berstatus
 * Selesai; selain itu kembali. Unduhan disimulasikan (FE saja).
 */
@Composable
fun ProjectCompletedContainer(
    projectId: Int,
    onBack: () -> Unit,
    onGiveReview: (projectId: Int) -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val payment = PaymentStore.forProject(projectId)
    if (ResultReviewStore.stageOf(projectId) != ResultReviewStage.COMPLETED || payment == null) {
        LaunchedEffect(projectId) { onBack() }
        return
    }
    val draft = ResultReviewStore.latestDraft(projectId)
    val files = remember(draft) { draft?.fileUis().orEmpty() }
    val sizes = draft?.files?.map { it.sizeBytes }.orEmpty()
    val profile = FreelancerProfileStore.profile
    val toast: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }

    ProjectCompletedScreen(
        state = ProjectCompletedUiState(
            freelancerName = profile.fullName,
            freelancerPhotoUri = profile.photoUri,
            files = files,
            totalSize = if (sizes.isNotEmpty() && sizes.all { it != null }) formatFileSize(sizes.sumOf { it ?: 0L }) else null,
            freelancerNet = formatRupiah(payment.freelancerNet),
            transactionId = payment.paymentId,
            rating = ResultReviewStore.review(projectId)?.rating ?: 0
        ),
        onBack = onBack,
        onDownloadFile = { toast("${it.name} diunduh (simulasi)") },
        onDownloadAll = { toast(if (files.isEmpty()) "Tidak ada berkas untuk diunduh" else "${files.size} berkas diunduh sebagai ZIP (simulasi)") },
        onGiveReview = { onGiveReview(projectId) },
        onBackToHome = onBackToHome
    )
}
