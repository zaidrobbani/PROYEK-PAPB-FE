package com.example.proyek_papb_fe.feature.freelancer.submission.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionStatusScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionStatusUiState
import com.example.proyek_papb_fe.feature.freelancer.submission.viewmodel.SubmissionStatusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private fun formatSubmittedAt(millis: Long): String {
    val format = SimpleDateFormat("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }
    return "${format.format(Date(millis))} WIB"
}

private fun formatSubmittedAgo(millis: Long, now: Long = System.currentTimeMillis()): String {
    val elapsed = (now - millis).coerceAtLeast(0)
    return when {
        elapsed < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
        elapsed < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(elapsed)} menit lalu"
        elapsed < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(elapsed)} jam lalu"
        else -> "${TimeUnit.MILLISECONDS.toDays(elapsed)} hari lalu"
    }
}

@Composable
fun SubmissionStatusContainer(
    projectId: String,
    onBack: () -> Unit,
    onOpenNegotiation: (projectId: String) -> Unit,
    onFindOtherProjects: () -> Unit,
    onViewProjectDetail: (projectId: String) -> Unit,
    onOpenWorkPlan: (projectId: String) -> Unit = {},
    viewModel: SubmissionStatusViewModel = viewModel(
        factory = SubmissionStatusViewModel.factory(projectId, AppContainer.projectRepository, AppContainer.workflowRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    val application = uiState.application
    if (application == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Status Pengajuan", onBack = onBack)
            if (uiState.isLoading) LoadingContent()
            else ErrorContent(uiState.errorMessage ?: "Pengajuan tidak ditemukan.", onRetry = viewModel::load)
        }
        return
    }
    val workflow = uiState.workflow
    val umkm = workflow?.umkm
    val title = workflow?.project?.title ?: uiState.project?.title ?: "Project"
    val clientName = umkm?.subtitle ?: umkm?.name ?: "UMKM TEMUKARYA"
    val submittedAt = application.submittedAtMillis ?: System.currentTimeMillis()
    val cancellationReason = when {
        uiState.isCancelled -> workflow?.cancellation?.reasonLabel ?: "Project dibatalkan"
        uiState.isRejected -> "UMKM memilih freelancer lain"
        else -> null
    }

    SubmissionStatusScreen(
        state = SubmissionStatusUiState(
            submittedAgo = formatSubmittedAgo(submittedAt),
            projectTitle = title,
            clientLine = listOfNotNull(clientName, umkm?.name?.takeIf { it != clientName }).joinToString(", "),
            clientName = clientName,
            offerAmount = rupiah(workflow?.agreement?.agreedPrice ?: application.proposedPrice),
            duration = "${workflow?.agreement?.durationDays ?: application.durationDays} hari",
            otherApplicants = "-",
            submittedAtLabel = formatSubmittedAt(submittedAt),
            isApproved = uiState.isSelected,
            cancellationReason = cancellationReason,
            isAgreed = uiState.isAgreed,
            isCompleted = uiState.isCompleted
        ),
        onBack = onBack,
        onOpenNegotiationClick = { onOpenNegotiation(projectId) },
        onFindOtherProjectsClick = onFindOtherProjects,
        onViewDetailClick = { onViewProjectDetail(projectId) },
        onOpenWorkPlanClick = { onOpenWorkPlan(projectId) }
    )
}
