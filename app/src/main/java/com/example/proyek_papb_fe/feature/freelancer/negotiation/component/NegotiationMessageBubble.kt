package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.data.model.NegotiationMessage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Figma: lebar teks bubble 250 + padding horizontal 14×2. */
private val BubbleMaxWidth = 278.dp

/**
 * Figma: Bubble (26:61, 26:67). Klien di kiri dengan avatar (putih + border),
 * freelancer (pengguna) di kanan tanpa avatar (cokelat).
 */
@Composable
internal fun NegotiationMessageBubble(
    message: NegotiationMessage,
    clientInitials: String,
    modifier: Modifier = Modifier
) {
    val isMine = message.sender == MessageSender.FREELANCER

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMine) {
            InitialsAvatar(
                initials = clientInitials,
                background = TemuKaryaColors.ChipGreen,
                textColor = NegotiationColors.OnGreen,
                size = 32.dp
            )
        }
        Column(
            modifier = Modifier
                .widthIn(max = BubbleMaxWidth)
                .then(
                    if (isMine) {
                        Modifier.background(TemuKaryaColors.ActionPrimary, NegotiationCardShape)
                    } else {
                        Modifier
                            .background(TemuKaryaColors.SurfaceWhite, NegotiationCardShape)
                            .border(1.dp, NegotiationColors.Outline, NegotiationCardShape)
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = message.text,
                style = NegotiationTypography.Message,
                color = if (isMine) TemuKaryaColors.OnAction else TemuKaryaColors.TextPrimary
            )
            Text(
                text = message.time,
                style = NegotiationTypography.MessageTime,
                color = if (isMine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.TextPlaceholder
            )
        }
    }
}

/** Figma: Chip "Hari ini" (26:59), pemisah tanggal di tengah. */
@Composable
internal fun DaySeparatorChip(label: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = label,
            style = NegotiationTypography.Chip,
            color = TemuKaryaColors.TextBody,
            modifier = Modifier
                .background(NegotiationColors.DayChip, androidx.compose.foundation.shape.CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
