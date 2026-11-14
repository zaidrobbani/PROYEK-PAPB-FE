package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

enum class StepState { DONE, CURRENT, UPCOMING, CANCELLED }

data class SubmissionStep(
    @param:DrawableRes val iconRes: Int,
    val title: String,
    val subtitle: String,
    val state: StepState
)

/**
 * Lima tahap pengajuan di kartu "Tahapan Pengajuan" (27:35), diturunkan dari satu state:
 * - [isApproved] = false: terkirim (selesai) → ditinjau UMKM (sekarang) → tiga tahap berikutnya.
 * - [isApproved] = true: ditinjau UMKM selesai, negosiasi & kesepakatan menjadi tahap sekarang.
 * - [cancellationReason] terisi: tahap negosiasi berakhir "Project dibatalkan" (tidak ada
 *   tahap "Sekarang"), tahap berikutnya tetap belum dimulai.
 * - [isAgreed] = true: kesepakatan final (filter "Project Berjalan"); negosiasi & pembayaran
 *   selesai, "Mulai pengerjaan" menjadi tahap sekarang.
 * - [isCompleted] = true: project selesai (filter "Selesai"); semua tahap selesai,
 *   termasuk pengerjaan.
 */
fun submissionSteps(
    submittedAtLabel: String,
    isApproved: Boolean,
    cancellationReason: String? = null,
    isAgreed: Boolean = false,
    isCompleted: Boolean = false
): List<SubmissionStep> = listOf(
    SubmissionStep(R.drawable.ic_check_circle_outline, "Penawaran terkirim", submittedAtLabel, StepState.DONE),
    if (isApproved) {
        SubmissionStep(R.drawable.ic_check_circle_outline, "Ditinjau pemilik UMKM", "Penawaranmu disetujui UMKM", StepState.DONE)
    } else {
        SubmissionStep(R.drawable.ic_pending, "Ditinjau pemilik UMKM", "UMKM sedang membandingkan pelamar", StepState.CURRENT)
    },
    if (cancellationReason != null) {
        SubmissionStep(R.drawable.ic_close, "Proyek dibatalkan", "Alasan: $cancellationReason", StepState.CANCELLED)
    } else {
        SubmissionStep(
            R.drawable.ic_handshake,
            "Negosiasi & kesepakatan",
            if (isAgreed) "Kesepakatan final tercapai" else "Diskusi detail, lalu lanjut atau batal",
            when {
                isAgreed -> StepState.DONE
                isApproved -> StepState.CURRENT
                else -> StepState.UPCOMING
            }
        )
    },
    SubmissionStep(
        R.drawable.ic_account_balance_wallet,
        "Pembayaran",
        "Dana ditahan TEMUKARYA",
        if (isAgreed) StepState.DONE else StepState.UPCOMING
    ),
    SubmissionStep(
        R.drawable.ic_brush,
        "Mulai pengerjaan",
        if (isCompleted) "Proyek selesai dikerjakan" else "Status menjadi Sedang Dikerjakan",
        when {
            isCompleted -> StepState.DONE
            isAgreed -> StepState.CURRENT
            else -> StepState.UPCOMING
        }
    )
)

/** Figma: kartu "Tahapan Pengajuan" (27:35). */
@Composable
internal fun SubmissionTimeline(
    steps: List<SubmissionStep>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceContainer, StatusCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        steps.forEach { step -> TimelineStep(step) }
    }
}

@Composable
private fun TimelineStep(step: SubmissionStep) {
    val iconTint = when (step.state) {
        StepState.DONE -> TemuKaryaColors.Olive
        StepState.CURRENT -> TemuKaryaColors.ActionPrimary
        StepState.UPCOMING -> SubmissionStatusColors.StepUpcomingIcon
        StepState.CANCELLED -> SubmissionStatusColors.CancelledChip
    }
    val isUpcoming = step.state == StepState.UPCOMING

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(step.iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(23.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = step.title,
                style = if (isUpcoming) SubmissionStatusTypography.StepTitleUpcoming
                else SubmissionStatusTypography.StepTitleActive,
                color = if (isUpcoming) TemuKaryaColors.TextPlaceholder else TemuKaryaColors.TextPrimary
            )
            Text(
                text = step.subtitle,
                style = SubmissionStatusTypography.Caption,
                color = TemuKaryaColors.TextBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (step.state == StepState.CURRENT) {
            Text(
                text = "Sekarang",
                style = SubmissionStatusTypography.Chip,
                color = TemuKaryaColors.ActionPrimary,
                modifier = Modifier
                    .background(TemuKaryaColors.SurfaceSand, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
