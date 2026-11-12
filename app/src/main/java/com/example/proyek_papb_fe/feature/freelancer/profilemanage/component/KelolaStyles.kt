package com.example.proyek_papb_fe.feature.freelancer.profilemanage.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/**
 * Token visual section Figma "6 · Profil (Freelancer) · Kelola" (Kelola Profil Profesional,
 * Kelola Portofolio, Kelola Pengalaman, Notifikasi). Ukuran 11.6–19 dengan line-height 1.4×
 * seperti layar Profil (27:250); didefinisikan lokal agar tema bersama tidak berubah.
 */
internal object KelolaTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp
    )

    val EmptyTitle = style(FontWeight.Bold, 19f)
    val SheetTitle = style(FontWeight.Bold, 16.9f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val CardTitle = style(FontWeight.Bold, 14.8f)
    val FieldValue = style(FontWeight.Normal, 14.8f)
    val EmptyBody = style(FontWeight.Normal, 14.8f)
    val ItemTitle = style(FontWeight.SemiBold, 14.8f)
    val NotifTitle = style(FontWeight.Bold, 13.7f)
    val ToggleTitle = style(FontWeight.SemiBold, 13.7f)
    val CheckLabel = style(FontWeight.Medium, 13.7f)
    val SheetBody = style(FontWeight.Normal, 13.7f)
    val Label = style(FontWeight.SemiBold, 12.7f)
    val Body = style(FontWeight.Normal, 12.7f)
    val BodyMedium = style(FontWeight.Medium, 12.7f)
    val Link = style(FontWeight.SemiBold, 12.7f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val Helper = style(FontWeight.Normal, 11.6f)
    val HelperError = style(FontWeight.Medium, 11.6f)
    val Caption = style(FontWeight.Medium, 11.6f)
    val ChipSmall = style(FontWeight.SemiBold, 10.6f)
    val Initials = style(FontWeight.Bold, 22f)
}

/** Warna dari variabel Figma (Color collection) yang belum ada di TemuKaryaColors. */
internal object KelolaColors {
    val Border = Color(0xFFE2D5BA) // color/border/default
    val Muted = Color(0xFF82746E) // m3/outline
    val Chip = Color(0xFFEDE0C2) // m3/surface-container-highest
    val SurfaceHigh = Color(0xFFF1E4C6) // m3/surface-container-high
    val SkillChip = Color(0xFFDBE3C4) // m3/secondary-container
    val OnSkillChip = Color(0xFF5E664D) // m3/on-secondary-container
    val Avatar = Color(0xFFDBE3C4)
    val OnAvatar = Color(0xFF5A6149) // m3/secondary
    val Error = Color(0xFFBA1A1A) // color/error/default
    val ErrorContainer = Color(0xFFFFDAD6) // m3/error-container
    val OnErrorContainer = Color(0xFF93000A) // m3/on-error-container
    val InverseSurface = Color(0xFF3F2C23) // m3/inverse-surface
    val InverseOnSurface = Color(0xFFFEF7E6) // m3/inverse-on-surface
    val InversePrimary = Color(0xFFEADBC8) // m3/inverse-primary
    val RevisionChip = Color(0xFFD08A6B) // color/status/revisi/bg
    val OnRevisionChip = Color(0xFFFFFFFF) // color/status/revisi/fg
    val BottomBar = Color(0xF7FEF7E6) // color/bg/canvas 97%
    val Scrim = Color(0x663E2B22) // #3E2B22 40%
    val SheetHandle = Color(0x6682746E) // m3/outline 40%
}

internal val KelolaCardShape = RoundedCornerShape(16.dp)
internal val KelolaFieldShape = RoundedCornerShape(12.dp)
internal val KelolaSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

/** Padding Main tiap layar Kelola: pt 84 − app bar 64 = 20, px 17, gap 16. */
internal val KelolaHorizontalPadding = 17.dp
internal val KelolaTopPadding = 20.dp
internal val KelolaSectionGap = 16.dp
/** Main pb 120 pada layar dengan Sticky Action Bar (±80). */
internal val KelolaBottomPaddingWithBar = 120.dp
internal val KelolaBottomPadding = 32.dp
