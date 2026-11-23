package com.example.proyek_papb_fe.feature.umkm.review.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.review.component.ProjectCompletedScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.ProjectCompletedUiState
import com.example.proyek_papb_fe.feature.umkm.review.component.formatFileSize
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ProjectResultViewModel
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ResultStage
import com.example.proyek_papb_fe.core.ui.components.rememberServerImageUri

@Composable
fun ProjectCompletedContainer(
    projectId: String,
    onBack: () -> Unit,
    onGiveReview: (projectId: String) -> Unit,
    onBackToHome: () -> Unit,
    viewModel: ProjectResultViewModel = viewModel(
        factory = ProjectResultViewModel.factory(
            projectId,
            AppContainer.workflowRepository,
            AppContainer.submissionRepository,
            AppContainer.fileRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    val detail = uiState.detail
    if (detail == null || uiState.stage != ResultStage.COMPLETED) {
        if (uiState.isLoading) LoadingContent()
        else ErrorContent(uiState.errorMessage ?: "Project belum selesai.", onRetry = viewModel::load)
        return
    }
    val draft = uiState.latest
    val files = remember(draft, uiState.localUris) { draft?.files?.map { it.toReviewFileUi(uiState.localUris[it.id]) }.orEmpty() }
    val payment = detail.payment

    ProjectCompletedScreen(
        state = ProjectCompletedUiState(
            freelancerName = detail.freelancer?.name ?: "Freelancer",
            freelancerPhotoUri = rememberServerImageUri(detail.freelancer?.photoUrl),
            files = files,
            totalSize = draft?.files?.takeIf { it.isNotEmpty() }?.let { list -> formatFileSize(list.sumOf { it.sizeBytes }) },
            freelancerNet = formatRupiah(payment?.freelancerAmount ?: detail.agreement?.freelancerAmount ?: 0),
            transactionId = payment?.transactionNumber ?: payment?.id.orEmpty(),
            rating = uiState.review?.rating ?: 0
        ),
        onBack = onBack,
        onDownloadFile = { file -> draft?.let { file.fileId(it.files, uiState.localUris) }?.let(viewModel::download) },
        onDownloadAll = viewModel::downloadAll,
        onGiveReview = {
            if (detail.hasReview) Toast.makeText(context, "Kamu sudah memberi ulasan untuk project ini.", Toast.LENGTH_SHORT).show()
            else onGiveReview(projectId)
        },
        onBackToHome = onBackToHome
    )
}
