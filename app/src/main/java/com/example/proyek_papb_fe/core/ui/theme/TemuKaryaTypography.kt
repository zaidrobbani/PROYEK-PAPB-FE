package com.example.proyek_papb_fe.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Text style TEMUKARYA dari Figma (ukuran px Figma = sp), font Plus Jakarta Sans. */
object TemuKaryaTypography {
    private fun style(weight: FontWeight, size: Float, lineHeight: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp
    )

    val TopBarTitle = style(FontWeight.Bold, 20f, 27f)
    val CardTitle = style(FontWeight.SemiBold, 19.015f, 26.146f)
    val Amount = style(FontWeight.Bold, 16.903f, 23.241f)
    val Input = style(FontWeight.Normal, 16.903f, 21f)
    val LabelSemiBold = style(FontWeight.SemiBold, 12.677f, 16.903f)
    val LabelMedium = style(FontWeight.Medium, 12.677f, 16.903f)
    val BodyRegular = style(FontWeight.Normal, 12.677f, 16.903f)
    val CaptionBold = style(FontWeight.Bold, 11.621f, 14.79f)
    val CaptionSemiBold = style(FontWeight.SemiBold, 11.621f, 14.79f)
    val CaptionMedium = style(FontWeight.Medium, 11.621f, 14.79f)
    val Avatar = style(FontWeight.Bold, 10.564f, 15.846f)
    val Counter = style(FontWeight.Normal, 9.508f, 9.508f)

    // Detail Project (17:4195)
    val DetailTopBarTitle = style(FontWeight.SemiBold, 18f, 24f)
    val SectionTitle = style(FontWeight.SemiBold, 16.903f, 23.241f)
    val SubsectionTitle = style(FontWeight.SemiBold, 14.79f, 21.128f)
    val MetricValue = style(FontWeight.Bold, 14.79f, 21.128f)
    val BodyLarge = style(FontWeight.Normal, 14.79f, 24.033f)
    val ButtonLabel = style(FontWeight.SemiBold, 14.79f, 19.015f)
    /** Inisial avatar besar (59dp); Figma tidak memberi spesifikasi teks, mengikuti skala judul. */
    val AvatarLarge = style(FontWeight.Bold, 19.015f, 26.146f)
}
