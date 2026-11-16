package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val BannerButtonShape = RoundedCornerShape(10.dp)

/** Figma: "Notifikasi Masuk" (27:63) dengan tombol "Buka" negosiasi. */
@Composable
internal fun NegotiationInviteBanner(
    clientName: String,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(StatusCardShape)
            .background(TemuKaryaColors.TextHeading)
            .clickable(onClick = onOpenClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(TemuKaryaColors.SurfaceSand, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notifications_active),
                contentDescription = null,
                tint = TemuKaryaColors.ActionPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Pengajuanmu disetujui!",
                style = SubmissionStatusTypography.BannerTitle,
                color = TemuKaryaColors.OnAction
            )
            Text(
                text = "$clientName mengajak negosiasi",
                style = SubmissionStatusTypography.Caption,
                color = TemuKaryaColors.SurfaceSand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "Buka",
            style = SubmissionStatusTypography.BannerButton,
            color = TemuKaryaColors.ActionPrimary,
            modifier = Modifier
                .clip(BannerButtonShape)
                .background(TemuKaryaColors.SurfaceWhite)
                .clickable(onClickLabel = "Buka negosiasi", onClick = onOpenClick)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
