package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

enum class DurationOption(
    val days: Int,
    val label: String,
    val isRecommended: Boolean = false,
    val isFullWidth: Boolean = false
) {
    SEVEN_DAYS(7, "7 Hari"),
    TEN_DAYS(10, "10 Hari"),
    FOURTEEN_DAYS(14, "14 Hari (Rekomendasi UMKM)", isRecommended = true, isFullWidth = true),
    TWENTY_ONE_DAYS(21, "21 Hari (Proyek Ekstensif)", isFullWidth = true);

    companion object {
        val Default = FOURTEEN_DAYS

        fun fromDays(days: Int): DurationOption? = entries.find { it.days == days }
    }
}

data class ProposalSnippet(val label: String, val text: String)

internal val DefaultProposalSnippets = listOf(
    ProposalSnippet(
        label = "Contoh Kemasan Kopi",
        text = "Saya lampirkan contoh desain kemasan kopi yang pernah saya kerjakan sebagai referensi."
    ),
    ProposalSnippet(
        label = "File siap cetak",
        text = "File akhir diserahkan siap cetak (CMYK 300 DPI) dalam format AI dan PDF."
    )
)

internal const val PlatformFeePercent = 5

internal fun platformFee(amount: Long): Long = amount * PlatformFeePercent / 100

internal fun netAmount(amount: Long): Long = amount - platformFee(amount)

internal fun formatThousands(amount: Long): String =
    amount.toString().reversed().chunked(3).joinToString(".").reversed()

internal fun formatRupiah(amount: Long): String = "Rp ${formatThousands(amount)}"

internal fun parseRupiah(text: String): Long = text.filter(Char::isDigit).toLongOrNull() ?: 0L

internal val CardShape = RoundedCornerShape(16.dp)
internal val InnerShape = RoundedCornerShape(8.451.dp)
internal val FieldShape = RoundedCornerShape(12.dp)

internal val CardPadding = 16.903.dp
internal val SmallGap = 4.226.dp

internal val AvatarOverlay = TemuKaryaColors.ActionPrimary.copy(alpha = 0.1f)

internal val BreakdownDivider = Color(0x66E2D5BA)

internal object OfferFormTypography {
    val OfferInput = TemuKaryaTypography.CardTitle.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 25.354.sp
    )

    val ProposalBody = TemuKaryaTypography.BodyLarge.copy(lineHeight = 21.128.sp)
}

internal fun Modifier.offerSubtleShadow(shape: Shape): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 1.056.dp,
        color = TemuKaryaColors.ShadowSubtle,
        offset = DpOffset(0.dp, 1.056.dp)
    )
)
