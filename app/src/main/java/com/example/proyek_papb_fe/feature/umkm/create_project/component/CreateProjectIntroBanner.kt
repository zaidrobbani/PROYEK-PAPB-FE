package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun CreateProjectIntroBanner(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(MaterialTheme.colorScheme.tertiaryContainer, shape)
            .padding(16.903.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.256.dp)
                .background(SageBadge, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_create_project_banner),
                contentDescription = null
            )
        }
        Text(
            text = "Buat Project Kolaborasi Baru",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.titleLarge.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.015.sp,
                lineHeight = 25.354.sp
            )
        )
    }
}
