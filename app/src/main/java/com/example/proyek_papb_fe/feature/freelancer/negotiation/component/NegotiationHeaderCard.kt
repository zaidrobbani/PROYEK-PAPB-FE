package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Pihak di negosiasi: UMKM atau freelancer. */
data class NegotiationParty(
    val initials: String,
    val name: String,
    val role: String
)

/** Figma: kartu konteks project (26:15) — chip status, judul, UMKM ⇄ freelancer. */
@Composable
internal fun NegotiationHeaderCard(
    projectTitle: String,
    client: NegotiationParty,
    freelancer: NegotiationParty,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, NegotiationCardShape)
            .border(1.dp, NegotiationColors.Outline, NegotiationCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            StatusChip(iconRes = R.drawable.ic_hourglass_top, label = "Menunggu Kesepakatan")
            StatusChip(iconRes = R.drawable.ic_schedule_rounded, label = "Belum dibayar")
        }

        Text(
            text = projectTitle,
            style = NegotiationTypography.CardTitle,
            color = TemuKaryaColors.TextHeading,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PartyInfo(
                party = client,
                avatarColor = TemuKaryaColors.SurfaceSand,
                avatarTextColor = TemuKaryaColors.ActionPrimary,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                painter = painterResource(R.drawable.ic_sync_alt),
                contentDescription = null,
                tint = TemuKaryaColors.TextPlaceholder,
                modifier = Modifier.size(19.dp)
            )
            PartyInfo(
                party = freelancer,
                avatarColor = TemuKaryaColors.ChipGreen,
                avatarTextColor = NegotiationColors.OnGreen,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
private fun StatusChip(@DrawableRes iconRes: Int, label: String) {
    Row(
        modifier = Modifier
            .background(NegotiationColors.StatusChip, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = NegotiationColors.OnStatusChip,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = label,
            style = NegotiationTypography.Chip,
            color = NegotiationColors.OnStatusChip
        )
    }
}

@Composable
private fun PartyInfo(
    party: NegotiationParty,
    avatarColor: Color,
    avatarTextColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(party.initials, avatarColor, avatarTextColor, size = 34.dp)
        Column {
            Text(
                text = party.name,
                style = NegotiationTypography.PartyName,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = party.role,
                style = NegotiationTypography.PartyRole,
                color = TemuKaryaColors.TextBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Avatar inisial (Figma: kotak radius 16 — praktis lingkaran pada ukuran 32–34). */
@Composable
internal fun InitialsAvatar(
    initials: String,
    background: Color,
    textColor: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(background, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = NegotiationTypography.Avatar,
            color = textColor
        )
    }
}
