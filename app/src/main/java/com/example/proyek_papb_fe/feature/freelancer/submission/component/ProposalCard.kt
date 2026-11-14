package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/**
 * Satu kartu = satu penawaran terkirim untuk satu project (bukan dikelompokkan per UMKM).
 * Ringkasan saja; detail tahapan ada di layar Status Pengajuan.
 * Gaya kartu mengikuti kartu ringkasan Status Pengajuan (27:15).
 */
@Composable
internal fun ProposalCard(
    item: ProposalListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(StatusCardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.dp, SubmissionStatusColors.Outline, StatusCardShape)
            .clickable(onClickLabel = "Lihat status proyek", onClick = onClick)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProposalStageChip(stage = item.stage)
            Box(modifier = Modifier.weight(1f))
            Text(
                text = item.submittedDate,
                style = SubmissionStatusTypography.CaptionMedium.withFigmaLineHeight(),
                color = TemuKaryaColors.TextPlaceholder,
                maxLines = 1
            )
        }

        Text(
            text = item.projectTitle,
            style = SubmissionStatusTypography.CardTitle.withFigmaLineHeight(),
            color = TemuKaryaColors.TextHeading,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Figma: ikon storefront dalam kotak 14dp (glyph ~11x10 di tengah).
            Box(modifier = Modifier.size(14.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_storefront),
                    contentDescription = null,
                    tint = TemuKaryaColors.TextBody
                )
            }
            Text(
                text = item.clientLine,
                style = SubmissionStatusTypography.Body.withFigmaLineHeight(),
                color = TemuKaryaColors.TextBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .height(1.dp)
                .background(SubmissionStatusColors.Outline)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.offerAmount,
                style = SubmissionStatusTypography.RowValueBold.withFigmaLineHeight(),
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1
            )
            Text(
                text = "·",
                style = SubmissionStatusTypography.RowLabel.withFigmaLineHeight(),
                color = TemuKaryaColors.TextPlaceholder
            )
            Text(
                text = item.duration,
                style = SubmissionStatusTypography.RowValue.withFigmaLineHeight(),
                color = TemuKaryaColors.TextBody,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = TemuKaryaColors.ActionPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ProposalStageChip(stage: ProposalStage) {
    when (stage) {
        ProposalStage.UNDER_REVIEW -> PendingStatusChip(text = stage.label)
        ProposalStage.NEGOTIATION -> ApprovedStatusChip(text = stage.label)
        ProposalStage.PLAN_SUBMITTED -> ApprovedStatusChip(
            text = stage.label,
            iconRes = R.drawable.ic_edit_note
        )
        ProposalStage.IN_PROGRESS -> ApprovedStatusChip(
            text = stage.label,
            iconRes = R.drawable.ic_check_circle_outline
        )
        ProposalStage.COMPLETED -> CompletedStatusChip()
        ProposalStage.CANCELLED -> CancelledStatusChip()
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ProposalCardPreview() {
    Column(
        modifier = Modifier
            .background(TemuKaryaColors.BgCanvas)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ProposalStage.entries.forEachIndexed { index, stage ->
            ProposalCard(
                item = ProposalListItem(
                    projectId = 101 + index,
                    stage = stage,
                    submittedDate = "24 Okt 2026",
                    projectTitle = "Desain Rebranding Logo & Kemasan Standing Pouch Kopi 250gr",
                    clientLine = "Kedai Kopi Senja · Bandung",
                    offerAmount = "Rp 1.500.000",
                    duration = "14 hari"
                ),
                onClick = {}
            )
        }
    }
}
