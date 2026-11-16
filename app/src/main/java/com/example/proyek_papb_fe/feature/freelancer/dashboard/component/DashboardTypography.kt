package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/** Text style dari Figma "Beranda Freelancer" (ukuran px Figma = sp). */
internal object DashboardTypography {
    private fun style(
        weight: FontWeight,
        size: Float,
        lineHeight: Float,
        letterSpacing: TextUnit = TextUnit.Unspecified
    ) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = letterSpacing
    )

    val Brand = style(FontWeight.Bold, 17f, 23f, 0.34.sp)
    val Greeting = style(FontWeight.Bold, 23.241f, 29.579f, (-0.581).sp)
    val SectionTitle = style(FontWeight.Bold, 19.015f, 25.354f)
    val Balance = style(FontWeight.Bold, 31.692f, 40.144f, (-0.7923).sp)
    val ActiveTitle = style(FontWeight.Bold, 16.903f, 23.241f)
    val CardTitle = style(FontWeight.Bold, 14.79f, 21.128f)
    val WalletTitle = style(FontWeight.SemiBold, 14.79f, 21.128f)
    val LabelSemiBold = style(FontWeight.SemiBold, 12.677f, 16.903f)
    val BodyRegular = style(FontWeight.Normal, 12.677f, 16.903f)
    val CaptionSemiBold = style(FontWeight.SemiBold, 11.621f, 14.79f)
    val CaptionMedium = style(FontWeight.Medium, 11.621f, 14.79f)
    val CaptionBold = style(FontWeight.Bold, 11.621f, 14.79f)
    val Badge = style(FontWeight.Bold, 10.564f, 15.846f, 0.2641.sp)
}
