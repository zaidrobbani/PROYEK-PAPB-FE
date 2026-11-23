package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarBorder
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.core.ui.theme.TextMuted
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ConversationStage
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversation
import com.example.proyek_papb_fe.feature.umkm.data.UmkmThread
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.components.rememberServerImageUri

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size, lineHeight = lineHeight)

@Composable
fun ConversationSearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val style = text(FontWeight.Normal, 14.sp, 20.sp)
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .dropShadow(shape, Shadow(radius = 8.45.dp, color = colors.primary.copy(alpha = 0.06f), offset = DpOffset(0.dp, 4.2.dp)))
            .background(colors.surfaceContainerLowest, shape)
            .semantics { contentDescription = "Cari percakapan" },
        singleLine = true,
        textStyle = style.copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        decorationBox = { inner ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_symbol_search), null, tint = colors.outline, modifier = Modifier.size(20.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Cari nama freelancer atau project", color = colors.outline, style = style, maxLines = 1)
                    }
                    inner()
                }
            }
        }
    )
}

@Composable
fun ConversationFilterPills(
    selected: ConversationStage?,
    totalCount: Int,
    countFor: (ConversationStage) -> Int,
    onSelect: (ConversationStage?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .selectableGroup()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(7.6.dp)
    ) {
        Pill("Semua ($totalCount)", selected == null) { onSelect(null) }
        ConversationStage.entries.forEach { stage ->
            Pill("${stage.label} (${countFor(stage)})", selected == stage) { onSelect(stage) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val requester = remember { BringIntoViewRequester() }
    LaunchedEffect(isSelected) { if (isSelected) requester.bringIntoView() }
    Text(
        text = label,
        modifier = Modifier
            .bringIntoViewRequester(requester)
            .then(
                if (isSelected) {
                    Modifier.dropShadow(CircleShape, Shadow(radius = 4.2.dp, color = colors.primary.copy(alpha = 0.18f), offset = DpOffset(0.dp, 2.1.dp)))
                } else Modifier
            )
            .clip(CircleShape)
            .background(if (isSelected) colors.primary else colors.tertiaryContainer)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.8.dp, vertical = 6.3.dp),
        color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
        maxLines = 1,
        style = text(FontWeight.SemiBold, 12.677.sp, 16.9.sp)
    )
}

@Composable
fun ConversationRow(conversation: UmkmThread, time: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val unread = conversation.unreadCount > 0
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val photo = rememberServerImageUri(conversation.photoUrl)
        if (photo != null) {
            LocalImage(
                uri = photo,
                fallbackRes = null,
                contentDescription = "Foto ${conversation.freelancerName}",
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SageBadge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(conversation.initials, color = colors.onSecondaryContainer, style = text(FontWeight.Bold, 14.sp, 18.sp))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .drawBehind {
                    val stroke = 1.dp.toPx()
                    val y = size.height - stroke / 2
                    drawLine(AppBarBorder, Offset(0f, y), Offset(size.width, y), stroke)
                }
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.freelancerName,
                    modifier = Modifier.weight(1f),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text(FontWeight.SemiBold, 15.sp, 20.sp)
                )
                Text(
                    text = time,
                    color = if (unread) colors.primary else colors.outline,
                    style = text(if (unread) FontWeight.SemiBold else FontWeight.Medium, 11.5.sp, 16.sp)
                )
            }
            Text(
                text = conversation.projectTitle,
                color = colors.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = text(FontWeight.Normal, 12.5.sp, 17.sp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.lastMessage,
                    modifier = Modifier.weight(1f),
                    color = if (unread) colors.onSurface else colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text(if (unread) FontWeight.Medium else FontWeight.Normal, 13.sp, 18.sp)
                )
                if (unread) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(colors.primary, CircleShape)
                            .semantics { contentDescription = "${conversation.unreadCount} pesan belum dibaca" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = colors.onPrimary,
                            style = text(FontWeight.Bold, 11.sp, 14.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationFootnote(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        Icon(painterResource(R.drawable.ic_symbol_info), null, tint = TextMuted, modifier = Modifier.size(15.dp))
        Text(
            text = "Percakapan terbuka setelah kamu memilih freelancer untuk sebuah project.",
            modifier = Modifier.width(300.dp),
            color = MaterialTheme.colorScheme.outline,
            style = text(FontWeight.Normal, 12.sp, 17.sp)
        )
    }
}
