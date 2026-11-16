package com.example.proyek_papb_fe.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Skema warna dari variabel Figma TEMUKARYA.
 * Container primary/tertiary memakai tonal palette Figma sesuai aturan nada Material 3,
 * karena frame Figma tidak memakainya secara langsung.
 */
private val TemuKaryaColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = FieldBackground,
    primaryContainer = Primary90,
    onPrimaryContainer = Primary10,
    inversePrimary = Primary80,
    secondary = M3Secondary,
    onSecondary = FieldBackground,
    secondaryContainer = FrameSecondaryContainer,
    onSecondaryContainer = FrameOnSecondaryContainer,
    tertiary = Tertiary40,
    onTertiary = FieldBackground,
    tertiaryContainer = BrandTertiary,
    onTertiaryContainer = FrameOnTertiaryContainer,
    background = FrameBackground,
    onBackground = TextPrimary,
    surface = FrameBackground,
    onSurface = M3OnSurface,
    surfaceVariant = FrameSurfaceVariant,
    onSurfaceVariant = M3OnSurfaceVariant,
    surfaceTint = BrandPrimary,
    inverseSurface = M3InverseSurface,
    inverseOnSurface = M3InverseOnSurface,
    error = ErrorDefault,
    onError = FieldBackground,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = FrameIcon,
    outlineVariant = BorderDefault,
    surfaceBright = FrameBackground,
    surfaceDim = BorderDefault,
    surfaceContainerLowest = FieldBackground,
    surfaceContainerLow = M3SurfaceContainerLow,
    surfaceContainer = BrandTertiary,
    surfaceContainerHigh = M3SurfaceContainerHigh,
    surfaceContainerHighest = FrameSurfaceVariant
)

/** Aplikasi hanya memakai mode terang sesuai desain Figma, tanpa warna dinamis Android 12+. */
@Composable
fun PROYEKPAPBFETheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TemuKaryaColorScheme,
        typography = Typography,
        content = content
    )
}
