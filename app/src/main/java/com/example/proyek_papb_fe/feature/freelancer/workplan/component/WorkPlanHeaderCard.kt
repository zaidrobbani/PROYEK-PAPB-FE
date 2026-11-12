package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Figma: kartu header (88:15) — tahap, chip status rencana, judul, klien & deadline. */
@Composable
internal fun WorkPlanHeaderCard(
    projectTitle: String,
    clientAndDeadline: String,
    isSubmitted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, WorkPlanCardShape)
            .border(1.dp, WorkPlanColors.Outline, WorkPlanCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Langkah sebelum pembayaran",
                style = WorkPlanTypography.Eyebrow,
                color = TemuKaryaColors.TextPlaceholder,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (isSubmitted) {
                WorkPlanChip(
                    iconRes = R.drawable.ic_hourglass_top,
                    label = "Menunggu UMKM",
                    container = WorkPlanColors.PendingChip,
                    content = WorkPlanColors.OnPendingChip
                )
            } else {
                WorkPlanChip(
                    iconRes = R.drawable.ic_edit_note,
                    label = "Draft",
                    container = WorkPlanColors.DraftChip,
                    content = WorkPlanColors.OnDraftChip
                )
            }
        }
        Text(
            text = projectTitle,
            style = WorkPlanTypography.CardTitle,
            color = TemuKaryaColors.TextHeading
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_event),
                contentDescription = null,
                tint = TemuKaryaColors.TextPlaceholder,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = clientAndDeadline,
                style = WorkPlanTypography.Meta,
                color = TemuKaryaColors.TextBody
            )
        }
    }
}

/** Figma: kartu petunjuk (88:25). */
@Composable
internal fun WorkPlanInfoCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WorkPlanColors.InfoCard, WorkPlanCardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info_outline),
            contentDescription = null,
            tint = TemuKaryaColors.Olive,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = "Susun tahapan pekerjaan beserta bobotnya. Total bobot harus 100%. " +
                "Rencana ini dikirim ke UMKM untuk disetujui sebelum pembayaran.",
            style = WorkPlanTypography.Meta,
            color = WorkPlanColors.OnInfoCard,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Figma: chip "Draft" (88:18) / "Siap diajukan" (88:87) — px 12, py 4, gap 4, ikon 15. */
@Composable
internal fun WorkPlanChip(
    @DrawableRes iconRes: Int,
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(container, WorkPlanChipShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(15.dp)
        )
        Text(text = label, style = WorkPlanTypography.Chip, color = content, maxLines = 1)
    }
}
