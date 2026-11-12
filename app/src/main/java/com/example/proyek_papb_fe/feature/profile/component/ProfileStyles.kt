package com.example.proyek_papb_fe.feature.profile.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/**
 * Token visual "Profil Saya (Freelancer)" (Figma 27:250). Frame ini memakai ukuran
 * 19/18/15.8/14.8/12.7/11.6 dengan line-height 1.4×, jadi didefinisikan lokal agar
 * tema bersama tidak berubah.
 */
internal object ProfileTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

    val Name = style(FontWeight.Bold, 19f)
    val Initials = style(FontWeight.Bold, 19f)
    val StatValue = style(FontWeight.Bold, 18f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val MenuTitle = style(FontWeight.SemiBold, 14.8f)
    val Headline = style(FontWeight.Normal, 12.7f)
    val Link = style(FontWeight.SemiBold, 12.7f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val Caption = style(FontWeight.Normal, 11.6f)
}

internal object ProfileColors {
    val Avatar = Color(0xFFDBE3C4)
    val OnAvatar = Color(0xFF424A33)
    val RatingChip = Color(0xFFEDE0C2)
    val AvailableChip = Color(0xFFC9D1B5)
    val OnAvailableChip = Color(0xFF364323)
    val SkillChip = Color(0xFFDBE3C4)
    val OnSkillChip = Color(0xFF424A33)
    val MenuIconBackground = Color(0xFFEDE0C2)
    val DangerBackground = Color(0xFFFFDAD6)
    val Danger = Color(0xFFBA1A1A)
    val CardBorder = Color(0xFFE2D5BA)
}

internal val ProfileCardShape = RoundedCornerShape(16.dp)
