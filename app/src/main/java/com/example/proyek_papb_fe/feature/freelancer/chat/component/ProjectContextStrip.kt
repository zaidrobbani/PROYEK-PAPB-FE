package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/**
 * Figma: Project Context (17:4612) — strip ringan di bawah app bar, bukan kartu:
 * chip dana tertahan, judul project satu baris, catatan tenggat, dan panah ke detail.
 */
@Composable
internal fun ProjectContextStrip(
    projectTitle: String,
    context: ChatProjectContext,
    onDetailClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    /**
     * Tahap negosiasi (Figma "Ruang Chat (Freelancer) · Negosiasi"): chip status dari
     * Negosiasi 26:2 menggantikan chip dana & baris catatan.
     */
    isNegotiation: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(TemuKaryaColors.Divider, Offset(0f, y), Offset(size.width, y), stroke)
            }
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isNegotiation) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NegotiationStatusChip(R.drawable.ic_hourglass_top, "Menunggu Kesepakatan")
                    NegotiationStatusChip(R.drawable.ic_schedule_rounded, "Belum dibayar")
                }
            } else {
                context.escrowLabel?.let { EscrowChip(it) }
            }
            Text(
                text = projectTitle,
                style = TemuKaryaTypography.SubsectionTitle,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!isNegotiation && context.note.isNotBlank()) Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_schedule_rounded),
                    contentDescription = null,
                    tint = TemuKaryaColors.TextPlaceholder,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append(context.note)
                        context.noteEmphasis?.let {
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = TemuKaryaColors.TextBody)) { append(it) }
                        }
                        append(context.noteSuffix)
                    },
                    style = TemuKaryaTypography.BodyRegular,
                    color = TemuKaryaColors.TextPlaceholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (onDetailClick != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClickLabel = "Lihat detail project", onClick = onDetailClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = "Lihat detail project",
                    tint = TemuKaryaColors.TextBody,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/** Chip status negosiasi (warna "menunggu" yang sama dengan Negosiasi 26:2 / Status Pengajuan). */
@Composable
private fun NegotiationStatusChip(iconRes: Int, label: String) {
    Row(
        modifier = Modifier
            .background(NegotiationColors.StatusChip, CircleShape)
            .padding(start = 10.dp, end = 12.dp, top = 3.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = NegotiationColors.OnStatusChip,
            modifier = Modifier.size(14.dp)
        )
        Text(text = label, style = TemuKaryaTypography.CaptionSemiBold, color = NegotiationColors.OnStatusChip, maxLines = 1)
    }
}

@Composable
private fun EscrowChip(label: String) {
    Row(
        modifier = Modifier
            .background(TemuKaryaColors.ChipGreen, CircleShape)
            .padding(start = 8.dp, end = 10.dp, top = 2.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_lock),
            contentDescription = null,
            tint = TemuKaryaColors.OnChipGreen,
            modifier = Modifier.size(13.dp)
        )
        Text(text = label, style = TemuKaryaTypography.CaptionSemiBold, color = TemuKaryaColors.OnChipGreen, maxLines = 1)
    }
}
