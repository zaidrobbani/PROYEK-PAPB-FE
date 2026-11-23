package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

internal object SubmissionStatusTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

    val CardTitle = style(FontWeight.Bold, 16.9f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val StepTitleActive = style(FontWeight.Bold, 14.8f)
    val StepTitleUpcoming = style(FontWeight.Medium, 14.8f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val RowLabel = style(FontWeight.Normal, 13.7f)
    val RowValueBold = style(FontWeight.Bold, 13.7f)
    val RowValue = style(FontWeight.SemiBold, 13.7f)
    val BannerTitle = style(FontWeight.Bold, 13.7f)
    val Body = style(FontWeight.Normal, 12.7f)
    val BannerButton = style(FontWeight.Bold, 12.7f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val CaptionMedium = style(FontWeight.Medium, 11.6f)
    val Caption = style(FontWeight.Normal, 11.6f)
}

internal object SubmissionStatusColors {
    val Outline = Color(0xFFE2D5BA)
    val PendingChip = Color(0xFFE8D9B0)
    val OnPendingChip = Color(0xFF544222)
    val CancelledChip = Color(0xFF9C8B82)
    val StepUpcomingIcon = Color(0xFFBFA69B)
    val BottomBarScrim = Color(0xF7FEF7E6)
}

internal fun TextStyle.withFigmaLineHeight(): TextStyle = copy(
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

internal val StatusCardShape = RoundedCornerShape(16.dp)
internal val StatusButtonShape = RoundedCornerShape(12.dp)
