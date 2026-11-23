package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

internal object WorkPlanTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

    val TotalValue = style(FontWeight.Bold, 23.2f)
    val CardTitle = style(FontWeight.Bold, 16.9f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val WeightInput = style(FontWeight.Bold, 14.8f)
    val NameInput = style(FontWeight.Normal, 13.7f)
    val FieldLabel = style(FontWeight.SemiBold, 12.7f)
    val Meta = style(FontWeight.Normal, 12.7f)
    val Count = style(FontWeight.SemiBold, 12.7f)
    val TotalLabel = style(FontWeight.Medium, 12.7f)
    val PercentSuffix = style(FontWeight.Medium, 12.7f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val Eyebrow = style(FontWeight.SemiBold, 11.6f)
    val Legend = style(FontWeight.Normal, 11.6f)
    val Footnote = style(FontWeight.Normal, 11.6f)
}

internal object WorkPlanColors {
    val Outline = Color(0xFFE2D5BA)
    val DraftChip = Color(0xFFECE2C8)
    val OnDraftChip = Color(0xFF4C4734)
    val InfoCard = Color(0x99DBE3C4)
    val OnInfoCard = Color(0xFF424A33)
    val ReadyChip = Color(0xFFC9D1B5)
    val OnReadyChip = Color(0xFF364323)
    val PendingChip = Color(0xFFE8D9B0)
    val OnPendingChip = Color(0xFF544222)
    val AddButton = Color(0x99FFFFFF)
    val AddButtonBorder = Color(0xFF82746E)
    val Track = Color(0xFFEDE0C2)
    val BottomBarScrim = Color(0xF7FEF7E6)
    val Error = Color(0xFFBA1A1A)

    val Segments = listOf(
        Color(0xFF684738),
        Color(0xFFAAB295),
        Color(0xFFD08A6B),
        Color(0xFFE9C46A),
        Color(0xFF5A6149),
        Color(0xFF82746E)
    )

    fun segment(index: Int): Color = Segments[index % Segments.size]
}

internal val WorkPlanCardShape = RoundedCornerShape(16.dp)
internal val WorkPlanFieldShape = RoundedCornerShape(12.dp)
internal val WorkPlanChipShape = RoundedCornerShape(999.dp)
