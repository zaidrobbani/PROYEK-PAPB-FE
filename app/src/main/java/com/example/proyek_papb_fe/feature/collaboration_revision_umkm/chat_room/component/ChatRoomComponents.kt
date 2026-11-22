package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarBorder
import com.example.proyek_papb_fe.core.ui.theme.AppBarIcon
import com.example.proyek_papb_fe.core.ui.theme.NavInactive
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ChatMessage

private val AttachmentIcon = Color(0xFFB8A68A)

private val BubbleMaxWidth = 264.dp + 28.dp

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size, lineHeight = lineHeight)

private fun Modifier.bottomBorder() = drawBehind {
    val stroke = 1.dp.toPx()
    val y = size.height - stroke / 2
    drawLine(AppBarBorder, Offset(0f, y), Offset(size.width, y), stroke)
}

private fun Modifier.topBorder() = drawBehind {
    val stroke = 1.dp.toPx()
    drawLine(AppBarBorder, Offset(0f, stroke / 2), Offset(size.width, stroke / 2), stroke)
}

@Composable
private fun IconButtonBox(
    @DrawableRes icon: Int,
    description: String,
    size: Dp,
    iconSize: Dp,
    tint: Color,
    background: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

@Composable
fun ChatRoomTopBar(
    initials: String,
    name: String,
    role: String,
    onBack: () -> Unit,
    onMore: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(colors.background)
            .bottomBorder()
            .padding(start = 4.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonBox(R.drawable.ic_symbol_arrow_back, "Kembali", 48.dp, 24.dp, AppBarIcon, Color.Transparent, onBack)
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(SandContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = colors.primary, style = text(FontWeight.Bold, 13.sp, 18.sp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, style = text(FontWeight.SemiBold, 15.sp, 20.sp))
            Text(role, color = NavInactive, maxLines = 1, style = text(FontWeight.Normal, 12.sp, 16.sp))
        }
        IconButtonBox(R.drawable.ic_symbol_more_vert, "Menu lainnya", 48.dp, 24.dp, AppBarIcon, Color.Transparent, onMore)
    }
}

@Composable
fun ChatProjectContext(
    projectTitle: String,
    heldFunds: String?,
    deadline: Pair<String, String>?,
    onOpenProject: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerLow)
            .bottomBorder()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (heldFunds != null) {
                Row(
                    modifier = Modifier
                        .background(SageBadge, CircleShape)
                        .padding(start = 8.dp, end = 10.dp, top = 2.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_symbol_lock), null, tint = OnSageBadge, modifier = Modifier.size(13.dp))
                    Text("Dana ditahan $heldFunds", color = OnSageBadge, style = text(FontWeight.SemiBold, 11.sp, 16.sp))
                }
            }
            Text(
                projectTitle,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = text(FontWeight.SemiBold, 14.5.sp, 20.sp)
            )
            if (deadline != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_symbol_schedule), null, tint = colors.outline, modifier = Modifier.size(14.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("Tenggat: ")
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.onSurfaceVariant)) { append(deadline.first) }
                            append(" (${deadline.second})")
                        },
                        color = colors.outline,
                        maxLines = 1,
                        style = text(FontWeight.Normal, 12.sp, 16.sp)
                    )
                }
            }
        }
        IconButtonBox(R.drawable.ic_symbol_chevron_right, "Lihat detail project", 40.dp, 24.dp, colors.onSurfaceVariant, Color.Transparent, onOpenProject)
    }
}

@Composable
fun ChatDateSeparator(label: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp), contentAlignment = Alignment.Center) {
        Text(
            text = label,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                .padding(horizontal = 10.dp, vertical = 3.dp),
            color = MaterialTheme.colorScheme.outline,
            style = text(FontWeight.Medium, 11.5.sp, 16.sp)
        )
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    time: String,
    senderLabel: String,
    showSender: Boolean,
    topPadding: Dp,
    onOpenDraft: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val outgoing = message.fromUmkm
    val shape = when {
        message.attachment != null -> RoundedCornerShape(18.dp)
        outgoing -> RoundedCornerShape(topStart = 18.dp, topEnd = 6.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
        else -> RoundedCornerShape(topStart = 6.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
    }
    val textColor = if (outgoing) colors.onPrimary else colors.onSurface
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = topPadding),
        horizontalAlignment = if (outgoing) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (!outgoing && showSender) {
            Text(senderLabel, modifier = Modifier.padding(start = 4.dp), color = colors.outline, style = text(FontWeight.Medium, 11.5.sp, 16.sp))
        }
        val bubble = Modifier
            .widthIn(max = BubbleMaxWidth)
            .clip(shape)
            .background(if (outgoing) colors.primary else colors.surfaceContainerLowest)
            .then(if (outgoing) Modifier else Modifier.border(1.dp, AppBarBorder, shape))
            .then(
                if (onOpenDraft != null) {
                    Modifier.clickable(role = Role.Button, onClickLabel = "Ajukan revisi draf", onClick = onOpenDraft)
                } else Modifier
            )
        if (message.attachment != null) {
            Column(modifier = bubble.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .background(colors.surfaceVariant, RoundedCornerShape(14.dp))
                        .semantics { contentDescription = "Pratinjau ${message.attachment.title}" },
                    contentAlignment = Alignment.Center
                ) {
                    val image = message.attachment.previewImage
                    if (image != null) {
                        Image(
                            painterResource(image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(painterResource(R.drawable.ic_symbol_image), null, tint = AttachmentIcon, modifier = Modifier.size(32.dp))
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 10.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_symbol_photo), null, tint = colors.outline, modifier = Modifier.size(16.dp))
                        Text(message.attachment.title, color = textColor, style = text(FontWeight.SemiBold, 12.5.sp, 18.sp))
                    }
                    Text(message.text, modifier = Modifier.fillMaxWidth(), color = textColor, style = text(FontWeight.Normal, 14.5.sp, 21.sp))
                    BubbleMeta(time, outgoing, message.readByFreelancer)
                }
            }
        } else {
            Column(
                modifier = bubble.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(message.text, color = textColor, style = text(FontWeight.Normal, 14.5.sp, 21.sp))
                BubbleMeta(time, outgoing, message.readByFreelancer)
            }
        }
    }
}

@Composable
private fun BubbleMeta(time: String, outgoing: Boolean, read: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            time,
            color = if (outgoing) SandContainer else MaterialTheme.colorScheme.outline,
            style = text(FontWeight.Medium, 11.sp, 14.sp)
        )
        if (outgoing && read) {
            Icon(painterResource(R.drawable.ic_symbol_done_all), "Dibaca", tint = SageBadge, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
fun ChatComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAdd: () -> Unit,
    onAttachImage: () -> Unit,
    onVoice: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val style = text(FontWeight.Normal, 14.5.sp, 20.sp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .topBorder()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        IconButtonBox(R.drawable.ic_symbol_add, "Aksi tambahan", 44.dp, 24.dp, colors.primary, colors.tertiaryContainer, onAdd)
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surfaceContainerLowest)
                .border(1.dp, AppBarBorder, RoundedCornerShape(22.dp))
                .padding(start = 16.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Tulis pesan" },
                singleLine = true,
                textStyle = style.copy(color = colors.onSurface),
                cursorBrush = SolidColor(colors.primary),
                decorationBox = { inner ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                "Tulis pesan atau diskusi proyek...",
                                color = colors.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = style
                            )
                        }
                        inner()
                    }
                }
            )
            IconButtonBox(R.drawable.ic_symbol_image, "Lampirkan gambar", 34.dp, 22.dp, colors.onSurfaceVariant, Color.Transparent, onAttachImage)
            IconButtonBox(R.drawable.ic_symbol_mic, "Pesan suara", 34.dp, 22.dp, colors.onSurfaceVariant, Color.Transparent, onVoice)
        }
        IconButtonBox(R.drawable.ic_symbol_send, "Kirim pesan", 44.dp, 20.dp, colors.onPrimary, colors.primary, onSend)
    }
}
