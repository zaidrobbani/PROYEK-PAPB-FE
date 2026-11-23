package com.example.proyek_papb_fe.feature.umkm.review.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.feature.umkm.review.component.FreelancerReviewScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.FreelancerReviewUiState
import com.example.proyek_papb_fe.feature.umkm.review.component.ReviewHighlightOptions
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ProjectResultViewModel
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ResultStage
import com.example.proyek_papb_fe.core.ui.components.rememberServerImageUri

@Composable
fun FreelancerReviewContainer(
    projectId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
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
    val freelancerName = uiState.detail?.freelancer?.name ?: "Freelancer"
    LaunchedEffect(uiState.reviewSent) {
        if (uiState.reviewSent) {
            viewModel.onReviewSentHandled()
            Toast.makeText(context, "Ulasan untuk $freelancerName terkirim", Toast.LENGTH_SHORT).show()
            onDone()
        }
    }

    val detail = uiState.detail
    if (detail == null || uiState.stage != ResultStage.COMPLETED) {
        if (uiState.isLoading) LoadingContent()
        else ErrorContent(uiState.errorMessage ?: "Ulasan hanya bisa diberikan setelah project selesai.", onRetry = viewModel::load)
        return
    }
    val existing = uiState.review
    val attachment = remember(uiState.latest, uiState.localUris) {
        uiState.latest?.files?.firstOrNull { it.mimeType.startsWith("image/") }?.let { file -> file.toReviewFileUi(uiState.localUris[file.id]) }
    }
    var rating by remember(existing) { mutableIntStateOf(existing?.rating ?: 0) }
    var highlights by remember(existing) {
        mutableStateOf(existing?.highlightLabels?.filter { it in ReviewHighlightOptions }?.toSet() ?: emptySet())
    }
    var text by remember(existing) { mutableStateOf(existing?.text.orEmpty()) }
    var includeAttachment by remember { mutableStateOf(true) }

    FreelancerReviewScreen(
        state = FreelancerReviewUiState(
            freelancerName = freelancerName,
            freelancerFirstName = firstName(freelancerName),
            freelancerPhotoUri = rememberServerImageUri(detail.freelancer?.photoUrl),
            projectTitle = detail.project.title,
            completedDate = formatShortDate(detail.project.completedAtMillis ?: System.currentTimeMillis()),
            rating = rating,
            highlights = highlights,
            text = text,
            attachment = attachment,
            includeAttachment = includeAttachment
        ),
        onBack = onBack,
        onRate = { rating = it },
        onToggleHighlight = { highlights = if (it in highlights) highlights - it else highlights + it },
        onTextChange = { text = it },
        onToggleAttachment = { includeAttachment = !includeAttachment },
        onSubmit = {
            when {
                existing != null -> Toast.makeText(context, "Ulasan untuk project ini sudah terkirim.", Toast.LENGTH_SHORT).show()
                rating in 1..5 -> viewModel.submitReview(rating, highlights, text, ReviewHighlightOptions)
            }
        },
        onSkip = onSkip
    )
}
