package com.example.proyek_papb_fe.feature.umkm.notifications.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.NotificationCategory
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotification

@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

@Composable
fun NotificationFilterChips(
    selected: NotificationCategory?,
    totalCount: Int,
    onSelect: (NotificationCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip("Semua ($totalCount)", selected == null) { onSelect(null) }
        NotificationCategory.entries.forEach { category ->
            FilterChip(category.label, selected == category) { onSelect(category) }
        }
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = label,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) colors.primary else colors.surfaceVariant)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
        style = text(FontWeight.SemiBold, 11.6f)
    )
}

@Composable
fun NotificationGroupLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline,
        style = text(FontWeight.SemiBold, 12.7f)
    )
}

@Composable
fun NotificationGroupCard(
    notifications: List<UmkmNotification>,
    timeLabel: (UmkmNotification) -> String,
    onClick: (UmkmNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(6.dp)
    ) {
        notifications.forEach { notification ->
            NotificationRow(
                notification = notification,
                time = timeLabel(notification),
                onClick = { onClick(notification) }
            )
        }
    }
}

@Composable
private fun NotificationRow(notification: UmkmNotification, time: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val unread = !notification.isRead
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (unread) colors.tertiaryContainer else Color.Transparent)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { stateDescription = if (unread) "Belum dibaca" else "Sudah dibaca" }
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(notification.type.icon),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = notification.title, color = colors.onSurface, style = text(FontWeight.Bold, 13.7f))
            Text(text = notification.body, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 12.7f))
            Text(text = time, color = colors.outline, style = text(FontWeight.Medium, 11.6f))
        }
        if (unread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(colors.error, CircleShape)
            )
        }
    }
}
