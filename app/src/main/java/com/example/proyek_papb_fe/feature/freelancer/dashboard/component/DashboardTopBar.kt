package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R

/** Figma: App Bar (162:175), varian Root/Brand. */
@Composable
internal fun DashboardTopBar(
    hasUnreadNotification: Boolean,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DashboardColors.BgCanvas)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(
                    color = DashboardColors.Divider,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = stroke
                )
            }
            .padding(start = 16.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(DashboardColors.ActionPrimary)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo_mark),
                contentDescription = null,
                modifier = Modifier.offset(x = 6.5.dp, y = 7.03.dp)
            )
        }

        Text(
            text = "TEMUKARYA",
            style = DashboardTypography.Brand,
            color = DashboardColors.ActionPrimary,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notifications),
                contentDescription = "Notifikasi",
                tint = DashboardColors.BellIcon,
                modifier = Modifier.size(24.dp)
            )
            if (hasUnreadNotification) {
                Image(
                    painter = painterResource(R.drawable.ic_notification_dot),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 28.dp, y = 12.dp)
                )
            }
        }
    }
}
