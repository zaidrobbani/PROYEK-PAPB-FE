package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

@Composable
internal fun SubmissionStatusChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null
) {
    Row(
        modifier = modifier
            .background(containerColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
        }
        Text(
            text = text,
            style = SubmissionStatusTypography.Chip,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
internal fun PendingStatusChip(
    modifier: Modifier = Modifier,
    text: String = "Menunggu Kesepakatan"
) {
    SubmissionStatusChip(
        text = text,
        containerColor = SubmissionStatusColors.PendingChip,
        contentColor = SubmissionStatusColors.OnPendingChip,
        iconRes = R.drawable.ic_hourglass_top,
        modifier = modifier
    )
}

@Composable
internal fun CancelledStatusChip(modifier: Modifier = Modifier) {
    SubmissionStatusChip(
        text = "Dibatalkan",
        containerColor = SubmissionStatusColors.CancelledChip,
        contentColor = TemuKaryaColors.OnAction,
        modifier = modifier
    )
}

@Composable
internal fun CompletedStatusChip(modifier: Modifier = Modifier) {
    SubmissionStatusChip(
        text = "Selesai",
        containerColor = TemuKaryaColors.Olive,
        contentColor = TemuKaryaColors.OnAction,
        iconRes = R.drawable.ic_done_all,
        modifier = modifier
    )
}

@Composable
internal fun ApprovedStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.ic_handshake
) {
    SubmissionStatusChip(
        text = text,
        containerColor = TemuKaryaColors.ChipGreen,
        contentColor = TemuKaryaColors.OnChipGreen,
        iconRes = iconRes,
        modifier = modifier
    )
}
