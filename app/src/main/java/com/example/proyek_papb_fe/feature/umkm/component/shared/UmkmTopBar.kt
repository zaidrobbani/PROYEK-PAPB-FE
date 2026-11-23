package com.example.proyek_papb_fe.feature.umkm.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarBorder
import com.example.proyek_papb_fe.core.ui.theme.AppBarIcon
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotificationStore

@Composable
fun UmkmTopBar(
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    hasUnreadNotification: Boolean = UmkmNotificationStore.unreadCount > 0
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(colors.background)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(AppBarBorder, Offset(0f, y), Offset(size.width, y), stroke)
            }
            .padding(start = 16.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (title == null) 10.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title == null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colors.primary, RoundedCornerShape(9.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_umkm_logo_mark),
                    contentDescription = "Logo TEMUKARYA",
                    modifier = Modifier.offset(x = 6.5.dp, y = 7.03.dp)
                )
            }
            Text(
                text = "TEMUKARYA",
                modifier = Modifier.weight(1f),
                color = colors.primary,
                style = MaterialTheme.typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 23.sp,
                    letterSpacing = 0.34.sp
                )
            )
        } else {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = colors.onSurface,
                style = MaterialTheme.typography.titleLarge.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 27.sp
                )
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onNotificationsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_symbol_notifications),
                contentDescription = "Notifikasi",
                tint = AppBarIcon,
                modifier = Modifier.size(24.dp)
            )
            if (hasUnreadNotification) {
                Image(
                    painter = painterResource(R.drawable.ic_umkm_notification_dot),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 28.dp, y = 12.dp)
                )
            }
        }
    }
}
