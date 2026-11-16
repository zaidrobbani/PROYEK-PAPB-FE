package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Figma: kartu ringkasan pengajuan (27:15). */
@Composable
internal fun SubmissionSummaryCard(
    submittedAgo: String,
    projectTitle: String,
    clientLine: String,
    offerAmount: String,
    duration: String,
    otherApplicants: String,
    isCancelled: Boolean = false,
    /** Penawaran disetujui UMKM (tahap Negosiasi). */
    isApproved: Boolean = false,
    /** Kesepakatan final tercapai (tahap Project Berjalan). */
    isAgreed: Boolean = false,
    /** Project selesai dikerjakan (tahap Selesai). */
    isCompleted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, StatusCardShape)
            .border(1.dp, SubmissionStatusColors.Outline, StatusCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Chip mengikuti tahap tab Project (sama dengan chip kartu di daftar).
            when {
                isCancelled -> CancelledStatusChip()
                isCompleted -> CompletedStatusChip()
                isAgreed -> ApprovedStatusChip(
                    text = ProposalStage.IN_PROGRESS.label,
                    iconRes = R.drawable.ic_check_circle_outline
                )
                isApproved -> ApprovedStatusChip(text = ProposalStage.NEGOTIATION.label)
                else -> PendingStatusChip()
            }
            Text(
                text = submittedAgo,
                style = SubmissionStatusTypography.CaptionMedium,
                color = TemuKaryaColors.TextPlaceholder
            )
        }

        Text(
            text = projectTitle,
            style = SubmissionStatusTypography.CardTitle,
            color = TemuKaryaColors.TextHeading,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = clientLine,
            style = SubmissionStatusTypography.Body,
            color = TemuKaryaColors.TextBody,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SubmissionStatusColors.Outline)
        )

        SummaryRow("Nilai penawaran", offerAmount, SubmissionStatusTypography.RowValueBold)
        SummaryRow("Durasi diajukan", duration, SubmissionStatusTypography.RowValue)
        SummaryRow("Pelamar lain", otherApplicants, SubmissionStatusTypography.RowValue)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueStyle: TextStyle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = SubmissionStatusTypography.RowLabel,
            color = TemuKaryaColors.TextBody
        )
        Text(
            text = value,
            style = valueStyle,
            color = TemuKaryaColors.TextPrimary
        )
    }
}
