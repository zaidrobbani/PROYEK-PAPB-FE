package com.example.proyek_papb_fe.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R

/** Font Figma TEMUKARYA. Lisensi: assets/licenses/plus_jakarta_sans_OFL.txt. */
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold)
)

private val Base = Typography()

/**
 * bodyMedium dan label* mengikuti token teks Figma (body/md, label/lg, label/md, label/sm).
 * Gaya lain tidak punya token di Figma, jadi memakai ukuran bawaan Material 3
 * dengan font Plus Jakarta Sans.
 */
val Typography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = PlusJakartaSans),
    displayMedium = Base.displayMedium.copy(fontFamily = PlusJakartaSans),
    displaySmall = Base.displaySmall.copy(fontFamily = PlusJakartaSans),
    headlineLarge = Base.headlineLarge.copy(fontFamily = PlusJakartaSans),
    headlineMedium = Base.headlineMedium.copy(fontFamily = PlusJakartaSans),
    headlineSmall = Base.headlineSmall.copy(fontFamily = PlusJakartaSans),
    titleLarge = Base.titleLarge.copy(fontFamily = PlusJakartaSans),
    titleMedium = Base.titleMedium.copy(fontFamily = PlusJakartaSans),
    titleSmall = Base.titleSmall.copy(fontFamily = PlusJakartaSans),
    bodyLarge = Base.bodyLarge.copy(fontFamily = PlusJakartaSans),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = Base.bodySmall.copy(fontFamily = PlusJakartaSans),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp
    )
)
