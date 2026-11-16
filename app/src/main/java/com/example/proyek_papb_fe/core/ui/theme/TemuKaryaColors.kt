package com.example.proyek_papb_fe.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palet brand TEMUKARYA dari Figma (variabel `color-*` + nilai tetap di frame).
 * Dipakai langsung oleh fitur karena MaterialTheme global masih memakai Dynamic Color.
 */
object TemuKaryaColors {
    // Figma variables
    val BgCanvas = Color(0xFFFEF7E6) // color-bg-canvas
    val ActionPrimary = Color(0xFF684738) // color-action-primary
    val TextPrimary = Color(0xFF3E2B22) // color-text-primary

    // Surfaces
    val SurfaceCard = Color(0xFFFBF3E0)
    val SurfaceContainer = Color(0xFFF7EDD3)
    val SurfaceSand = Color(0xFFEADBC8)
    val SurfaceWhite = Color(0xFFFFFFFF)

    // Text
    val TextHeading = Color(0xFF4E3123)
    val TextBody = Color(0xFF50443F)
    val TextPlaceholder = Color(0xFF82746E)
    val TextStrong = Color(0xFF2D1509)
    val OnAction = Color(0xFFFFFFFF)

    // Accent
    val ChipGreen = Color(0xFFDBE3C4)
    val OnChipGreen = Color(0xFF5E664D)
    val Olive = Color(0xFF5A6149)
    val BellIcon = Color(0xFF6B5A50)

    // Lines
    val Divider = Color(0xFFEADFC6)
    val MetricDivider = Color(0x99E2D5BA) // rgba(226,213,186,0.6)

    // Shadows (rgba 104,71,56)
    val ShadowCard = Color(0x14684738) // 0.08
    val ShadowSoft = Color(0x0F684738) // 0.06
    val ShadowPill = Color(0x2E684738) // 0.18
    val ShadowSubtle = Color(0x0D000000) // rgba(0,0,0,0.05)
    val ShadowButton = Color(0x1A000000) // rgba(0,0,0,0.1)

    // Sticky bottom action bar: rgba(254,247,230,0.95)
    val ActionBarScrim = Color(0xF2FEF7E6)
}
