package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: App Bar varian Chat (161:44) — kembali, avatar, nama kontak + UMKM, menu. */
@Composable
internal fun ChatRoomTopBar(
    contactName: String,
    contactInitials: String,
    umkmName: String,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(TemuKaryaColors.BgCanvas)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(TemuKaryaColors.Divider, Offset(0f, y), Offset(size.width, y), stroke)
            }
            .padding(start = 4.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconAction(R.drawable.ic_arrow_back, "Kembali", onBack)
        InitialsAvatar(
            initials = contactInitials,
            size = 38,
            background = TemuKaryaColors.SurfaceSand,
            textColor = TemuKaryaColors.ActionPrimary,
            style = TemuKaryaTypography.LabelSemiBold
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contactName,
                style = TemuKaryaTypography.SubsectionTitle,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = umkmName,
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextPlaceholder,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconAction(R.drawable.ic_more_vert, "Menu lainnya", onMoreClick)
    }
}

@Composable
private fun IconAction(iconRes: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = description,
            tint = TemuKaryaColors.BellIcon,
            modifier = Modifier.size(24.dp)
        )
    }
}
