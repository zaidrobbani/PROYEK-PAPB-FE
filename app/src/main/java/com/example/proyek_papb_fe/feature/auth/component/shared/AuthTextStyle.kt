package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Menyesuaikan gaya teks dari tema dengan nilai pada frame Figma.
 * Font tetap mengikuti tema, jadi bukan token typography baru.
 */
internal fun TextStyle.withFrameMetrics(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = 0.sp
): TextStyle = copy(
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing
)
