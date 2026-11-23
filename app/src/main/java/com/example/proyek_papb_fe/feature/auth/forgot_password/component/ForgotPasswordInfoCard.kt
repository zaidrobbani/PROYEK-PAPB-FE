package com.example.proyek_papb_fe.feature.auth.forgot_password.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun ForgotPasswordInfoCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SageBadge.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_symbol_info),
            contentDescription = null,
            tint = colors.secondary,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = "Tautan reset berlaku 30 menit. Periksa juga folder spam jika email belum masuk.",
            modifier = Modifier.weight(1f),
            color = colors.onSecondaryContainer,
            style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 12.7.sp,
                lineHeight = 1.4.em
            )
        )
    }
}
