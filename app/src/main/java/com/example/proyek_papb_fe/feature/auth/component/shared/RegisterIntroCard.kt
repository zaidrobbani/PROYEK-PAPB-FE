package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R

@Composable
fun RegisterIntroCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(colors.tertiaryContainer, shape)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(12.677.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.img_register_owner),
            contentDescription = null,
            modifier = Modifier
                .size(50.708.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Akun Baru",
                    color = colors.onSurface,
                    style = typography.titleLarge.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.015.sp,
                        lineHeight = 25.354.sp
                    )
                )
                Image(
                    painter = painterResource(R.drawable.ic_register_verified),
                    contentDescription = null
                )
            }
            Text(
                text = "Mulai kolaborasi hebat bersama talenta terbaik",
                color = colors.onSurfaceVariant,
                style = typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
    }
}
