package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

internal object NegotiationTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

    val CardTitle = style(FontWeight.Bold, 16.9f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val RowLabel = style(FontWeight.Normal, 13.7f)
    val RowValueBold = style(FontWeight.Bold, 13.7f)
    val RowValue = style(FontWeight.SemiBold, 13.7f)
    val Message = style(FontWeight.Normal, 13.7f)
    val PartyName = style(FontWeight.SemiBold, 12.7f)
    val Avatar = style(FontWeight.Bold, 12.7f)
    val Info = style(FontWeight.Normal, 12.7f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val PartyRole = style(FontWeight.Normal, 11.6f)
    val MessageTime = style(FontWeight.Medium, 10.6f)

    val SheetTitle = style(FontWeight.Bold, 18f)
    val SheetSubtitle = style(FontWeight.Normal, 12.7f)
    val SheetBody = style(FontWeight.Normal, 13.7f)
    val FieldLabel = style(FontWeight.SemiBold, 12.7f)
    val OptionSelected = style(FontWeight.SemiBold, 13.7f)
    val Option = style(FontWeight.Normal, 13.7f)
}

internal object NegotiationColors {
    val Outline = Color(0xFFE2D5BA)
    val StatusChip = Color(0xFFE8D9B0)
    val OnStatusChip = Color(0xFF544222)
    val DayChip = Color(0xFFEDE0C2)
    val OnGreen = Color(0xFF424A33)
    val InfoCard = Color(0x99DBE3C4)
    val Danger = Color(0xFFBA1A1A)
    val BottomBarScrim = Color(0xF7FEF7E6)
    val DangerContainer = Color(0xFFFFDAD6)
    val SheetScrim = Color(0x663E2B22)
    val SheetHandle = Color(0x6682746E)
}

internal val NegotiationCardShape = RoundedCornerShape(16.dp)
internal val NegotiationButtonShape = RoundedCornerShape(12.dp)
internal val NegotiationSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
