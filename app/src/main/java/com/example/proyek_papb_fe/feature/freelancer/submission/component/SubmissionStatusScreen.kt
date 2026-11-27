package com.example.proyek_papb_fe.feature.freelancer.submission.component

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import kotlinx.coroutines.Job
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

data class SubmissionStatusUiState(
    val submittedAgo: String,
    val projectTitle: String,
    val clientLine: String,
    val clientName: String,
    val offerAmount: String,
    val duration: String,
    val otherApplicants: String,
    val submittedAtLabel: String,

    val isApproved: Boolean,

    val cancellationReason: String? = null,
    val isAgreed: Boolean = false,
    val isCompleted: Boolean = false
) {
    val isCancelled: Boolean get() = cancellationReason != null
}

@Composable
fun SubmissionStatusScreen(
    state: SubmissionStatusUiState,
    onBack: () -> Unit,
    onOpenNegotiationClick: () -> Unit,
    onOpenWorkPlanClick: () -> Unit = {},
    onFindOtherProjectsClick: () -> Unit,
    onViewDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Job)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Status Pengajuan", onBack = onBack)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            RefreshableContent(onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SubmissionSummaryCard(
                        submittedAgo = state.submittedAgo,
                        projectTitle = state.projectTitle,
                        clientLine = state.clientLine,
                        offerAmount = state.offerAmount,
                        duration = state.duration,
                        otherApplicants = state.otherApplicants,
                        isCancelled = state.isCancelled,
                        isApproved = state.isApproved,
                        isAgreed = state.isAgreed,
                        isCompleted = state.isCompleted
                    )
                    Text(
                        text = "Tahapan Pengajuan",
                        style = SubmissionStatusTypography.SectionTitle,
                        color = TemuKaryaColors.TextHeading
                    )
                    SubmissionTimeline(
                        steps = remember(
                            state.submittedAtLabel,
                            state.isApproved,
                            state.cancellationReason,
                            state.isAgreed,
                            state.isCompleted
                        ) {
                            submissionSteps(
                                state.submittedAtLabel,
                                state.isApproved,
                                state.cancellationReason,
                                state.isAgreed,
                                state.isCompleted
                            )
                        }
                    )
                    AnimatedVisibility(
                        visible = state.isApproved && !state.isCancelled && !state.isAgreed,
                        enter = slideInHorizontally(
                            animationSpec = tween(durationMillis = 450),
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) + fadeIn(animationSpec = tween(durationMillis = 450))
                    ) {
                        NegotiationInviteBanner(
                            clientName = state.clientName,
                            onOpenClick = onOpenNegotiationClick
                        )
                    }
                    if (state.isAgreed && !state.isCancelled) {
                        NegotiationInviteBanner(
                            clientName = state.clientName,
                            onOpenClick = onOpenWorkPlanClick,
                            title = if (state.isCompleted) "Proyek selesai" else "Proyek sudah disepakati",
                            subtitle = if (state.isCompleted) "Lihat milestone & status dana" else "Mulai kerja & tandai milestone selesai",
                            buttonLabel = "Rencana"
                        )
                    }
                }
            }

            SubmissionStatusActionBar(
                onFindOtherProjectsClick = onFindOtherProjectsClick,
                onViewDetailClick = onViewDetailClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
