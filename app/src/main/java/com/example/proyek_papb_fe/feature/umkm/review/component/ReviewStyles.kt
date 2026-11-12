package com.example.proyek_papb_fe.feature.umkm.review.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/** Tipografi tambahan frame review (17:1468, 17:5518, 17:6140, 17:6338, 17:6591); sisanya memakai PaymentTypography. */
internal object ReviewTypography {
    private fun style(weight: FontWeight, size: Float, line: Float, tracking: Float = 0f, italic: Boolean = false) =
        TextStyle(
            fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp,
            letterSpacing = tracking.sp, fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
        )

    val Heading = style(FontWeight.SemiBold, 19.015f, 26.146f)
    val SheetTitle = style(FontWeight.SemiBold, 19.015f, 25.354f, tracking = -0.4754f)
    val Quote = style(FontWeight.Normal, 14.79f, 24.033f, italic = true)
    val Instruction = style(FontWeight.Normal, 14.79f, 24.033f)
    val Textarea = style(FontWeight.Normal, 14.79f, 21.128f)
    val SmallBody = style(FontWeight.Normal, 12.677f, 17.431f)
    val Note = style(FontWeight.Normal, 12.677f, 20.6f)
    val ButtonBold = style(FontWeight.Bold, 14.79f, 19.015f)
    val CaptionBold = style(FontWeight.Bold, 11.621f, 14.79f)
    val Tiny = style(FontWeight.Normal, 10.564f, 15.846f)
    val Pill = style(FontWeight.SemiBold, 12.677f, 16.903f, tracking = 0.3169f)
    val Score = style(FontWeight.Bold, 14.79f, 21.128f)
}

internal object ReviewColors {
    val GreenChip = Color(0xFFDEE6C7)
    val OnGreenChip = Color(0xFF171E0A)
    val PendingChip = Color(0xFFECE2C8)
    val PendingDot = Color(0xFF544E3B)
    val OnPending = Color(0xFF3C3725)
    val TanBox = Color(0xFFEADBC8)
    val Muted = Color(0xFF82746E)
    val Star = Color(0xFFC8962E)
    val StarEmpty = Color(0xFFCFC6AD)
    val Grip = Color(0xFFEDE0C2)
    val GripApprove = Color(0xFFE2D5BA)
    val SheetFooter = Color(0xFFFFF8F6)
    val Secondary = Color(0xFFFFE2D6)
    val OnSecondary = Color(0xFF281810)
    val DeepBrown = Color(0xFF5F3F31)
    val OnFreelancerChip = Color(0xFF424A33)
    val RatingCallout = Color(0x99EDE0C2) // rgba(237,224,194,0.6)
    val Scrim = Color(0x663F2C23) // rgba(63,44,35,0.4)
    val BottomBar = Color(0xF2FEF7E6) // 95%
    val BottomBarCompleted = Color(0xE6FEF7E6) // 90%
    val ShadowWarm = Color(0x14684738) // rgba(104,71,56,0.08)
}

internal val ReviewSheetShape = RoundedCornerShape(topStart = 29.579.dp, topEnd = 29.579.dp)

/** Tombol ikon bulat (latar [background]). */
@Composable
internal fun RoundIconButton(
    @DrawableRes iconRes: Int,
    size: Dp,
    iconWidth: Dp,
    iconHeight: Dp,
    background: Color,
    tint: Color,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickable(role = Role.Button, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(iconRes), contentDescription, tint = tint, modifier = Modifier.size(width = iconWidth, height = iconHeight))
    }
}

/** Baris 5 bintang; [rating] 0..5, [onRate] null = hanya tampilan. */
@Composable
internal fun StarRow(
    rating: Int,
    starWidth: Dp,
    starHeight: Dp,
    gap: Dp,
    padding: Dp,
    onRate: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
        (1..5).forEach { star ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .then(if (onRate != null) Modifier.clickable(role = Role.Button, onClickLabel = "$star bintang") { onRate(star) } else Modifier)
                    .padding(start = padding, end = padding, top = padding, bottom = padding * 2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_ul_star),
                    contentDescription = null,
                    tint = if (star <= rating) ReviewColors.Star else ReviewColors.StarEmpty,
                    modifier = Modifier.size(width = starWidth, height = starHeight)
                )
            }
        }
    }
}

/** "Puas Sekali" dst. untuk label rating singkat (Figma 17:6140 "5.0 (Puas Sekali)"). */
internal fun shortRatingLabel(rating: Int): String = when (rating) {
    5 -> "Puas Sekali"
    4 -> "Puas"
    3 -> "Cukup"
    2 -> "Kurang"
    1 -> "Kecewa"
    else -> "Belum dinilai"
}

/** Kalimat penilaian (Figma 17:6591 "Luar Biasa Memuaskan! Sangat Direkomendasikan"). */
internal fun sentimentLabel(rating: Int): String = when (rating) {
    5 -> "Luar Biasa Memuaskan! Sangat Direkomendasikan"
    4 -> "Memuaskan, Direkomendasikan"
    3 -> "Cukup Baik"
    2 -> "Kurang Memuaskan"
    1 -> "Tidak Memuaskan"
    else -> "Pilih bintang untuk menilai"
}

/** Ikon & latar berkas menurut ekstensi (Figma: PDF, vector/AI, dokumen lain). */
internal data class FileVisual(@param:DrawableRes val iconRes: Int, val background: Color, val iconWidth: Dp, val iconHeight: Dp)

internal fun fileVisual(extension: String, index: Int): FileVisual = when (extension) {
    "ai", "eps", "svg" -> FileVisual(R.drawable.ic_rv_vector, Color(0xFFEADBC8), 18.399.dp, 17.431.dp)
    "pdf" -> FileVisual(R.drawable.ic_dn_pdf, Color(0xFFDEE6C7), 19.368.dp, 19.368.dp)
    // Glyph arsip dari ekspor 17:1468, dipakai untuk berkas terkompresi.
    "png", "jpg", "jpeg", "webp", "gif", "heic" -> FileVisual(R.drawable.ic_rv_image, Color(0xFFECE2C8), 19.368.dp, 19.368.dp)
    "zip", "rar", "7z" -> FileVisual(R.drawable.ic_rv_pdf, Color(0xFFECE2C8), 19.368.dp, 19.368.dp)
    else -> FileVisual(R.drawable.ic_rv_guide, listOf(Color(0xFFECE2C8), Color(0xFFEADBC8), Color(0xFFDEE6C7))[index % 3], 21.304.dp, 15.494.dp)
}

/** 18400000 → "18.4 MB"; null = "Ukuran tidak diketahui". */
internal fun formatFileSize(bytes: Long?): String = when {
    bytes == null -> "Ukuran tidak diketahui"
    bytes >= 1_000_000 -> String.format(java.util.Locale.US, "%.1f MB", bytes / 1_000_000.0)
    bytes >= 1_000 -> "${bytes / 1_000} KB"
    else -> "$bytes B"
}
