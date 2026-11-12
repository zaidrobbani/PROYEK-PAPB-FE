package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans

/**
 * Token visual frame "Rencana Pengerjaan (Freelancer)" (88:2): ukuran teks 11.6–23.2,
 * line-height 1.4×. Lokal agar tema bersama tidak berubah.
 */
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
    /** Border kartu header, input, dan garis atas bottom bar. */
    val Outline = Color(0xFFE2D5BA)
    /** Chip "Draft". */
    val DraftChip = Color(0xFFECE2C8)
    val OnDraftChip = Color(0xFF4C4734)
    /** Kartu petunjuk: rgba(219,227,196,0.6). */
    val InfoCard = Color(0x99DBE3C4)
    val OnInfoCard = Color(0xFF424A33)
    /** Chip "Siap diajukan". */
    val ReadyChip = Color(0xFFC9D1B5)
    val OnReadyChip = Color(0xFF364323)
    /** Chip saat total bobot belum 100% (tidak ada di Figma; mengikuti chip status Negosiasi). */
    val PendingChip = Color(0xFFE8D9B0)
    val OnPendingChip = Color(0xFF544222)
    /** Tombol "Tambah Milestone": rgba(255,255,255,0.6), garis putus-putus #82746E. */
    val AddButton = Color(0x99FFFFFF)
    val AddButtonBorder = Color(0xFF82746E)
    /** Track progres bobot. */
    val Track = Color(0xFFEDE0C2)
    /** Bottom bar: rgba(254,247,230,0.97). */
    val BottomBarScrim = Color(0xF7FEF7E6)
    /** Border input yang belum valid (setelah percobaan ajukan). */
    val Error = Color(0xFFBA1A1A)

    /**
     * Warna segmen per milestone, urut seperti Figma (88:91–88:94). Milestone ke-5 dst.
     * memakai warna tambahan dari palet brand, lalu berulang.
     */
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
