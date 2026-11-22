package com.example.proyek_papb_fe.feature.auth.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 12.677.dp)
                .size(67.61.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .background(colors.primary, RoundedCornerShape(14.494.dp))
                    .padding(horizontal = 5.488.dp, vertical = 7.546.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_temukarya_logo),
                    contentDescription = "Logo TEMUKARYA",
                    modifier = Modifier.size(width = 36.934.dp, height = 35.968.dp)
                )
            }
        }
        Text(
            text = "Selamat Datang Kembali",
            color = colors.onSurface,
            textAlign = TextAlign.Center,
            style = typography.headlineSmall.withFrameMetrics(
                fontWeight = FontWeight.Bold,
                fontSize = 23.241.sp,
                lineHeight = 29.579.sp
            )
        )
        Text(
            text = "Masuk untuk memantau project dan\nkolaborasi aktifmu",
            modifier = Modifier
                .padding(top = 4.226.dp)
                .widthIn(max = 295.795.dp)
                .padding(horizontal = 23.833.dp),
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = typography.bodyMedium.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
        )
    }
}
