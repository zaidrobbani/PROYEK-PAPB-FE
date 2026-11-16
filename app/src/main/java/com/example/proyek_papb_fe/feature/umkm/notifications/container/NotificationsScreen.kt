package com.example.proyek_papb_fe.feature.umkm.notifications.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.NotificationCategory
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotification
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotificationStore
import com.example.proyek_papb_fe.feature.umkm.data.formatNotificationTime
import com.example.proyek_papb_fe.feature.umkm.data.notificationGroupLabel
import com.example.proyek_papb_fe.feature.umkm.notifications.component.NotificationFilterChips
import com.example.proyek_papb_fe.feature.umkm.notifications.component.NotificationGroupCard
import com.example.proyek_papb_fe.feature.umkm.notifications.component.NotificationGroupLabel
import java.time.LocalDateTime

/**
 * Notifikasi UMKM (Blueprint TK-F-U-26). Data berasal dari DATA DUMMY lokal
 * [UmkmNotificationStore]; membuka notifikasi menandainya sudah dibaca.
 */
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onNotificationClick: (UmkmNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by rememberSaveable { mutableStateOf<String?>(null) }
    val category = categoryName?.let { NotificationCategory.valueOf(it) }

    val all = UmkmNotificationStore.notifications.sortedByDescending { it.time }
    val shown = all.filter { category == null || it.type.category == category }
    val groups = shown.groupBy { it.time.toLocalDate() }
    val now = LocalDateTime.now()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Notifikasi", onBack = onBack)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 84.dp - 64.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "chips") {
                NotificationFilterChips(
                    selected = category,
                    totalCount = all.size,
                    onSelect = { categoryName = it?.name }
                )
            }
            if (shown.isEmpty()) {
                item(key = "empty") {
                    // Figma tidak punya keadaan kosong; teks sederhana ini tambahan.
                    Text(
                        text = "Belum ada notifikasi di kategori ini.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 33.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.7.sp,
                            lineHeight = 17.78.sp
                        )
                    )
                }
            }
            groups.forEach { (day, items) ->
                item(key = "label-$day") {
                    NotificationGroupLabel(notificationGroupLabel(day, now.toLocalDate()))
                }
                item(key = "group-$day") {
                    NotificationGroupCard(
                        notifications = items,
                        timeLabel = { formatNotificationTime(it.time, now) },
                        onClick = { notification ->
                            UmkmNotificationStore.markRead(notification.id)
                            onNotificationClick(notification)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NotificationsScreenPreview() {
    PROYEKPAPBFETheme {
        NotificationsScreen(onBack = {}, onNotificationClick = {})
    }
}
