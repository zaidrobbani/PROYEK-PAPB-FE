package com.example.proyek_papb_fe.feature.umkm.dashboard.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun DashboardSectionHeader(
    title: String,
    actionLabel: String,
    actionColor: Color,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = typography.titleSmall.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
        )
        Text(
            text = actionLabel,
            modifier = Modifier.clickable(role = Role.Button, onClick = onActionClick),
            color = actionColor,
            style = typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}
