package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Tautan "Sudah punya akun? Masuk", ukuran bawaan mengikuti frame Pilih Peran. */
@Composable
fun AuthLoginLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    linkFontSize: TextUnit = 12.677.sp,
    linkLineHeight: TextUnit = 16.903.sp,
    verticalPadding: Dp = 6.338.dp
) {
    AuthPromptLink(
        prompt = "Sudah punya akun?",
        action = "Masuk",
        onClick = onClick,
        modifier = modifier,
        actionColor = MaterialTheme.colorScheme.onTertiaryContainer,
        actionFontSize = linkFontSize,
        actionLineHeight = linkLineHeight,
        verticalPadding = verticalPadding
    )
}

/** Teks ajakan dengan tautan bergaris bawah, misalnya "Belum punya akun? Daftar". */
@Composable
fun AuthPromptLink(
    prompt: String,
    action: String,
    onClick: () -> Unit,
    actionColor: Color,
    modifier: Modifier = Modifier,
    actionFontSize: TextUnit = 14.79.sp,
    actionLineHeight: TextUnit = 21.128.sp,
    spacing: Dp = 4.226.dp,
    verticalPadding: Dp = 0.dp
) {
    val typography = MaterialTheme.typography
    val underlineColor = actionColor.copy(alpha = 0.4f)

    Row(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prompt,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = typography.bodyMedium.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
        )
        Text(
            text = action,
            modifier = Modifier.drawBehind {
                val y = size.height - 1.dp.toPx()
                drawLine(
                    color = underlineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            },
            color = actionColor,
            style = typography.labelMedium.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = actionFontSize,
                lineHeight = actionLineHeight
            )
        )
    }
}
