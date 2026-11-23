package com.example.proyek_papb_fe.feature.auth.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun LoginAlternativeOption(
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.056.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.056.dp)
                    .background(colors.outlineVariant.copy(alpha = 0.6f))
            )
            Text(
                text = "atau masuk dengan Google",
                modifier = Modifier
                    .wrapContentHeight(unbounded = true)
                    .background(colors.surfaceContainerLow)
                    .padding(horizontal = 8.451.dp),
                color = colors.onSurfaceVariant,
                style = typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.708.dp)
                .clip(shape)
                .background(colors.surfaceContainerLowest)
                .clickable(role = Role.Button, onClick = onGoogleClick),
            horizontalArrangement = Arrangement.spacedBy(12.677.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_login_google),
                contentDescription = null
            )
            Text(
                text = "Akun Google",
                color = colors.onSurface,
                style = typography.labelLarge.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
        }
    }
}
