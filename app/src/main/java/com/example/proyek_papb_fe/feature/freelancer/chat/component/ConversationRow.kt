package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/**
 * Figma: Chat List Item (272:21). Baris pesan ala WhatsApp, bukan kartu:
 * avatar | nama UMKM + jam / judul project / pesan terakhir + badge belum dibaca.
 * Garis pemisah tipis dimulai setelah avatar.
 */
@Composable
internal fun ConversationRow(
    item: ChatListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnread = item.unreadCount > 0
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Buka percakapan", onClick = onClick)
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        InitialsAvatar(
            initials = item.initials,
            size = 48,
            background = TemuKaryaColors.ChipGreen,
            textColor = TemuKaryaColors.OnChipGreen,
            style = TemuKaryaTypography.MetricValue
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .drawBehind {
                    val stroke = 1.dp.toPx()
                    val y = size.height - stroke / 2
                    drawLine(TemuKaryaColors.Divider, Offset(0f, y), Offset(size.width, y), stroke)
                }
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.umkmName,
                    style = TemuKaryaTypography.SubsectionTitle,
                    color = TemuKaryaColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.timeLabel,
                    style = if (isUnread) TemuKaryaTypography.CaptionSemiBold else TemuKaryaTypography.CaptionMedium,
                    color = if (isUnread) TemuKaryaColors.ActionPrimary else TemuKaryaColors.TextPlaceholder,
                    maxLines = 1
                )
            }
            Text(
                text = item.projectTitle,
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextPlaceholder,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.messagePreview,
                    style = if (isUnread) TemuKaryaTypography.LabelMedium else TemuKaryaTypography.BodyRegular,
                    color = if (isUnread) TemuKaryaColors.TextPrimary else TemuKaryaColors.TextBody,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isUnread) UnreadBadge(count = item.unreadCount)
            }
        }
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
            .background(TemuKaryaColors.ActionPrimary, CircleShape)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = TemuKaryaTypography.CaptionBold,
            color = TemuKaryaColors.OnAction
        )
    }
}

@Composable
internal fun InitialsAvatar(
    initials: String,
    size: Int,
    background: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, style = style, color = textColor, maxLines = 1)
    }
}

@Preview(showBackground = true, widthDp = 412, backgroundColor = 0xFFFEF7E6)
@Composable
private fun ConversationRowPreview() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ConversationRow(
            item = ChatListItem(1, "KS", "Kedai Kopi Senja", "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
                "Anda: Tentu Bu Rina, nanti saya buatkan 2 opsi ketebalan font", "09.48", 0),
            onClick = {}
        )
        ConversationRow(
            item = ChatListItem(3, "SM", "Toko Kue Sari Manis", "Desain Kemasan Kue Kering Lebaran",
                "Boleh kirim contoh kemasan yang pernah Mas buat?", "08.50", 2),
            onClick = {}
        )
    }
}
