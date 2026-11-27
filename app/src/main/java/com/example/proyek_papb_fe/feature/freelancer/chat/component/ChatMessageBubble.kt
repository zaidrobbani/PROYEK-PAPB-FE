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
import com.example.proyek_papb_fe.core.ui.components.chat.ChatDocumentAttachment
import com.example.proyek_papb_fe.core.ui.components.chat.ChatFileUi
import com.example.proyek_papb_fe.core.ui.components.chat.ChatImageAttachment
import com.example.proyek_papb_fe.core.ui.components.chat.DeletedMessageText
import com.example.proyek_papb_fe.core.ui.components.chat.selectableMessage
import com.example.proyek_papb_fe.core.ui.components.chat.DeliveryIcon
import com.example.proyek_papb_fe.core.ui.components.chat.DeliveryStatus
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val BubbleMaxWidth = 292.dp
private val AttachmentWidth = 264.dp

data class ChatBubbleUi(
    val id: String,
    val isMine: Boolean,
    val text: String,
    val time: String,
    val isFirstInGroup: Boolean,
    val topGap: Int,
    val senderLabel: String? = null,
    val file: ChatFileUi? = null,
    /** null untuk pesan masuk. */
    val status: DeliveryStatus? = if (isMine) DeliveryStatus.SENT else null,
    /** Dihapus untuk semua orang. */
    val isDeleted: Boolean = false,
    /** false untuk lampiran yang masih dikirim (belum punya id server). */
    val isSelectable: Boolean = true
)

sealed interface ChatRoomItem {
    val key: String
    data class DateSeparator(val label: String) : ChatRoomItem { override val key = "date-$label" }
    /** Penanda pergantian project saat chat menggabungkan beberapa project dengan UMKM yang sama. */
    data class ProjectSeparator(val title: String, override val key: String) : ChatRoomItem
    data class Message(val bubble: ChatBubbleUi) : ChatRoomItem { override val key = bubble.id }
}

@Composable
internal fun ChatMessageBubble(
    bubble: ChatBubbleUi,
    modifier: Modifier = Modifier,
    isOpening: Boolean = false,
    onFileClick: (ChatBubbleUi) -> Unit = {},
    isSelected: Boolean = false,
    selectionMode: Boolean = false,
    onToggleSelect: (ChatBubbleUi) -> Unit = {}
) {
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
    val file = bubble.file?.takeUnless { bubble.isDeleted }
    val isImage = file?.isImage == true
    val selectable = bubble.isSelectable
    val longPress: (() -> Unit)? = if (selectable) ({ onToggleSelect(bubble) }) else null
    val fileClick = { if (selectionMode && selectable) onToggleSelect(bubble) else onFileClick(bubble) }
    val status = bubble.status ?: DeliveryStatus.SENT

    Column(
        modifier = modifier
            .padding(top = bubble.topGap.dp)
            .fillMaxWidth()
            .selectableMessage(isSelected, selectionMode, selectable) { onToggleSelect(bubble) }
            .padding(vertical = 2.dp),
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
                    if (file != null) Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
                    else Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp)
                ),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (file != null) 8.dp else 4.dp)
        ) {
            if (file != null) {
                if (isImage) {
                    ChatImageAttachment(file = file, status = status, width = AttachmentWidth, onClick = fileClick, onLongClick = longPress)
                } else {
                    ChatDocumentAttachment(
                        file = file,
                        mine = mine,
                        status = status,
                        isOpening = isOpening,
                        onClick = fileClick,
                        modifier = Modifier.width(AttachmentWidth),
                        onLongClick = longPress
                    )
                }
                Column(
                    modifier = Modifier
                        .width(AttachmentWidth)
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (bubble.text.isNotBlank()) {
                        Text(text = bubble.text, style = TemuKaryaTypography.BodyLarge, color = textColor, modifier = Modifier.fillMaxWidth())
                    }
                    MessageMeta(time = bubble.time, status = bubble.status)
                }
            } else {
                if (bubble.isDeleted) {
                    DeletedMessageText(mine = mine, color = textColor)
                } else if (bubble.text.isNotBlank()) {
                    Text(text = bubble.text, style = TemuKaryaTypography.BodyLarge, color = textColor)
                }
                // Pesan terhapus tidak menampilkan status baca, seperti WhatsApp.
                MessageMeta(time = bubble.time, status = bubble.status.takeUnless { bubble.isDeleted }, mine = mine)
            }
        }
    }
}

@Composable
private fun MessageMeta(time: String, status: DeliveryStatus?, mine: Boolean = status != null) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = time,
            style = TemuKaryaTypography.CaptionMedium,
            color = if (mine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.TextPlaceholder
        )
        if (status != null) DeliveryIcon(status = status, sentTint = TemuKaryaColors.SurfaceSand)
    }
}

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
