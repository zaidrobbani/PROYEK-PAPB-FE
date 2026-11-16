package com.example.proyek_papb_fe.feature.umkm.notification_detail.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.umkm.data.NotificationTarget
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotificationStore
import com.example.proyek_papb_fe.feature.umkm.notification_detail.component.NotificationDetailCard
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FullTime = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, HH.mm", Locale.forLanguageTag("id-ID"))

/**
 * Detail Notifikasi UMKM (Blueprint TK-F-U-26). Tidak ada frame Figma; gaya mengikuti kartu di
 * Notifikasi UMKM. Tombol aksi membuka layar yang dirujuk notifikasi bila layarnya tersedia.
 */
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onBack: () -> Unit,
    onOpenTarget: (NotificationTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val notification = UmkmNotificationStore.notifications.firstOrNull { it.id == notificationId }
    LaunchedEffect(notificationId) { UmkmNotificationStore.markRead(notificationId) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AuthAppBar(title = "Detail Notifikasi", onBack = onBack)
        if (notification == null) {
            // Tidak ada di Figma: pengaman bila notifikasi tidak ditemukan.
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Notifikasi tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Column
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NotificationDetailCard(
                notification = notification,
                timeLabel = "${notification.time.format(FullTime)} WIB"
            )
            notification.target?.let { target ->
                AuthPrimaryButton(
                    text = target.actionLabel,
                    onClick = { onOpenTarget(target) },
                    shadowStyle = AuthShadowStyle.None,
                    height = 51.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NotificationDetailScreenPreview() {
    PROYEKPAPBFETheme {
        NotificationDetailScreen("dummy-notif-pengajuan", onBack = {}, onOpenTarget = {})
    }
}
