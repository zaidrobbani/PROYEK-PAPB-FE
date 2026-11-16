package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: teks maks 264 + padding 14×2 (±71% lebar layar 412). */
private val BubbleMaxWidth = 292.dp
private val AttachmentWidth = 264.dp

/** Data tampilan satu pesan di ruang chat. */
data class ChatBubbleUi(
    val id: String,
    val isMine: Boolean,
    val text: String,
    /** "09.15 WIB". */
    val time: String,
    /** Pesan pertama dari pengirim ini setelah pengirim lain / pemisah tanggal. */
    val isFirstInGroup: Boolean,
    /** Jarak atas: 0 setelah pemisah tanggal, 12 saat pengirim berganti, 4 untuk pesan lanjutan. */
    val topGap: Int,
    /** Label pengirim di atas pesan masuk pertama dalam grup; null = tidak ditampilkan. */
    val senderLabel: String? = null,
    val attachmentName: String? = null,
    /** true = lampiran gambar (pratinjau besar); false = dokumen (baris file ringkas). */
    val attachmentIsImage: Boolean = true
)

/** Item di daftar pesan: pemisah tanggal atau bubble. */
sealed interface ChatRoomItem {
    val key: String
    data class DateSeparator(val label: String) : ChatRoomItem { override val key = "date-$label" }
    data class Message(val bubble: ChatBubbleUi) : ChatRoomItem { override val key = bubble.id }
}

/**
 * Figma: Message / Masuk & Message / Keluar (17:4612).
 * Masuk = putih + garis tipis di kiri; keluar = cokelat di kanan dengan centang baca.
 * Selain warna, pengirim dibedakan lewat posisi dan sudut kecil (6) di sisi pengirim
 * pada bubble pertama tiap grup.
 */
@Composable
internal fun ChatMessageBubble(bubble: ChatBubbleUi, modifier: Modifier = Modifier) {
    val mine = bubble.isMine
    val small = 6.dp
    val large = 18.dp
    val shape = RoundedCornerShape(
        topStart = if (!mine && bubble.isFirstInGroup) small else large,
        topEnd = if (mine && bubble.isFirstInGroup) small else large,
        bottomEnd = large,
        bottomStart = large
    )
    val textColor = if (mine) TemuKaryaColors.OnAction else TemuKaryaColors.TextPrimary
    val isImage = bubble.attachmentName != null && bubble.attachmentIsImage

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = bubble.topGap.dp),
        horizontalAlignment = if (mine) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        bubble.senderLabel?.let {
            Text(
                text = it,
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextPlaceholder,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Column(
            modifier = Modifier
                .widthIn(max = BubbleMaxWidth)
                .then(
                    if (mine) Modifier.background(TemuKaryaColors.ActionPrimary, shape)
                    else Modifier
                        .background(TemuKaryaColors.SurfaceWhite, shape)
                        .border(1.dp, TemuKaryaColors.Divider, shape)
                )
                .then(
                    if (isImage) Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
                    else Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp)
                ),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isImage) 8.dp else 4.dp)
        ) {
            val name = bubble.attachmentName
            if (name != null && isImage) {
                AttachmentPreview()
                Column(
                    modifier = Modifier
                        .width(AttachmentWidth)
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AttachmentNameRow(
                        name = name,
                        iconRes = R.drawable.ic_image,
                        mine = mine,
                        textColor = textColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (bubble.text.isNotBlank()) {
                        Text(text = bubble.text, style = TemuKaryaTypography.BodyLarge, color = textColor, modifier = Modifier.fillMaxWidth())
                    }
                    MessageMeta(time = bubble.time, mine = mine)
                }
            } else {
                if (name != null) AttachmentNameRow(name = name, iconRes = R.drawable.ic_document, mine = mine, textColor = textColor)
                if (bubble.text.isNotBlank()) {
                    Text(text = bubble.text, style = TemuKaryaTypography.BodyLarge, color = textColor)
                }
                MessageMeta(time = bubble.time, mine = mine)
            }
        }
    }
}

@Composable
private fun AttachmentNameRow(
    name: String,
    iconRes: Int,
    mine: Boolean,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier.widthIn(max = AttachmentWidth)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (mine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.TextPlaceholder,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = name,
            style = TemuKaryaTypography.LabelSemiBold,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AttachmentPreview() {
    Box(
        modifier = Modifier
            .width(AttachmentWidth)
            .height(168.dp)
            .background(TemuKaryaColors.SurfaceSand, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_image),
            contentDescription = "Pratinjau lampiran gambar",
            tint = TemuKaryaColors.TextPlaceholder,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun MessageMeta(time: String, mine: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = time,
            style = TemuKaryaTypography.CaptionMedium,
            color = if (mine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.TextPlaceholder
        )
        if (mine) {
            Icon(
                painter = painterResource(R.drawable.ic_done_all),
                contentDescription = "Terbaca",
                tint = TemuKaryaColors.ChipGreen,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/** Figma: Date Separator — label kecil di tengah, bukan kartu. */
@Composable
internal fun ChatDateSeparator(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TemuKaryaTypography.CaptionMedium,
            color = TemuKaryaColors.TextPlaceholder,
            modifier = Modifier
                .background(TemuKaryaColors.SurfaceContainer, CircleShape)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}
