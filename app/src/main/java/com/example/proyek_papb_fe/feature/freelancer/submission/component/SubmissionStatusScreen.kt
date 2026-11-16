package com.example.proyek_papb_fe.feature.freelancer.submission.component

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

/** Data tampilan (sudah diformat) untuk layar Status Pengajuan. */
data class SubmissionStatusUiState(
    val submittedAgo: String,
    val projectTitle: String,
    val clientLine: String,
    val clientName: String,
    val offerAmount: String,
    val duration: String,
    val otherApplicants: String,
    val submittedAtLabel: String,
    /**
     * false = "Ditinjau pemilik UMKM" (banner tersembunyi);
     * true = disetujui: tahap "Negosiasi & kesepakatan" + banner "Pengajuanmu disetujui!".
     * Timeline dan banner sama-sama diturunkan dari nilai ini agar tidak pernah tidak sinkron.
     */
    val isApproved: Boolean,
    /**
     * Terisi jika project dibatalkan dari Negosiasi (label alasan); null = masih aktif.
     * Saat dibatalkan banner negosiasi disembunyikan agar project tidak bisa dinegosiasikan lagi.
     */
    val cancellationReason: String? = null,
    /** true = kesepakatan final tercapai (filter "Project Berjalan"): project resmi dikerjakan. */
    val isAgreed: Boolean = false,
    /** true = project selesai dikerjakan (filter "Selesai"); semua tahap tuntas. */
    val isCompleted: Boolean = false
) {
    val isCancelled: Boolean get() = cancellationReason != null
}

/**
 * Figma: Status Pengajuan (Menunggu Kesepakatan) (27:2).
 * Read-only; top bar dan bottom bar tetap, konten di-scroll di antaranya.
 */
@Composable
fun SubmissionStatusScreen(
    state: SubmissionStatusUiState,
    onBack: () -> Unit,
    onOpenNegotiationClick: () -> Unit,
    onFindOtherProjectsClick: () -> Unit,
    onViewDetailClick: () -> Unit,
    modifier: Modifier = Modifier
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    // Figma: Main pt 84 (app bar 64 + 20), px 17, pb 120 (bottom bar 80 + 40).
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
                // Banner masuk dari kiri saat disetujui. Jika layar dibuka ulang dalam
                // keadaan sudah disetujui, banner langsung tampil tanpa animasi ulang.
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
            }

            SubmissionStatusActionBar(
                onFindOtherProjectsClick = onFindOtherProjectsClick,
                onViewDetailClick = onViewDetailClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
