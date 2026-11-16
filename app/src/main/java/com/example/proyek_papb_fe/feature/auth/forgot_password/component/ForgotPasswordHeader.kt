package com.example.proyek_papb_fe.feature.auth.forgot_password.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun ForgotPasswordHeader(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(SandContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_symbol_lock_reset),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(31.dp)
            )
        }
        Text(
            text = "Atur ulang kata sandi",
            color = colors.onTertiaryContainer,
            style = typography.headlineSmall.withFrameMetrics(
                fontWeight = FontWeight.Bold,
                fontSize = 23.2.sp,
                lineHeight = 1.4.em
            )
        )
        Text(
            text = "Masukkan email yang terdaftar. Kami akan mengirim tautan untuk membuat kata sandi baru.",
            color = colors.onSurfaceVariant,
            style = typography.bodyMedium.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.8.sp,
                lineHeight = 1.4.em
            )
        )
    }
}
