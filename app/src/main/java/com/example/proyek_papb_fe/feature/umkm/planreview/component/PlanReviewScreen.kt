package com.example.proyek_papb_fe.feature.umkm.planreview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Satu milestone yang ditinjau UMKM (read-only). */
data class ReviewMilestoneUi(val name: String, val weightPercent: Int)

/** Data tampilan Tinjau Rencana Pengerjaan (sudah diformat). */
data class PlanReviewUiState(
    val projectTitle: String,
    /** "Diajukan Andi Ramadhan, deadline 7 Nov 2026 (14 hari)". */
    val submittedBy: String,
    val milestones: List<ReviewMilestoneUi>
) {
    val totalWeight: Int get() = milestones.sumOf { it.weightPercent }
}

private object PlanReviewTypography {
    private fun style(weight: FontWeight, size: Float) =
        TextStyle(fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp)

    val Eyebrow = style(FontWeight.SemiBold, 11.6f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val Title = style(FontWeight.Bold, 16.9f)
    val Body = style(FontWeight.Normal, 12.7f)
    val Section = style(FontWeight.Bold, 15.8f)
    val Count = style(FontWeight.SemiBold, 12.7f)
    val Label = style(FontWeight.SemiBold, 12.7f)
    val Input = style(FontWeight.Normal, 13.7f)
    val Weight = style(FontWeight.Bold, 14.8f)
    val Percent = style(FontWeight.Medium, 12.7f)
    val TotalLabel = style(FontWeight.Medium, 12.7f)
    val Total = style(FontWeight.Bold, 23.2f)
    val Legend = style(FontWeight.Normal, 11.6f)
    val Button = style(FontWeight.SemiBold, 14.8f)
}

private object PlanReviewColors {
    val Muted = Color(0xFF82746E)
    val Border = Color(0xFFE2D5BA)
    val PendingChip = Color(0xFFECE2C8)
    val OnPendingChip = Color(0xFF4C4734)
    val Info = Color(0x99DBE3C4) // rgba(219,227,196,0.6)
    val InfoIcon = Color(0xFF5A6149)
    val OnInfo = Color(0xFF424A33)
    val Track = Color(0xFFEDE0C2)
    val ValidChip = Color(0xFFC9D1B5)
    val OnValidChip = Color(0xFF364323)
    val BottomBar = Color(0xF7FEF7E6)

    /** Warna segmen bar bobot berurutan (Figma 270:741–744). */
    val Segments = listOf(Color(0xFF684738), Color(0xFFAAB295), Color(0xFFD08A6B), Color(0xFFE9C46A))
}

private val CardShape = RoundedCornerShape(16.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val WeightColumn = 92.dp

/**
 * Figma: Tinjau Rencana Pengerjaan (UMKM) (270:663). UMKM memeriksa milestone yang diajukan
 * freelancer (read-only), lalu "Setujui Rencana" (lanjut ke Pembayaran 17:1267) atau
 * "Minta Ubah". Sticky Bottom Bar dua tombol; isi di-scroll (pt 20, px 17, gap 16, pb 120).
 */
@Composable
fun PlanReviewScreen(
    state: PlanReviewUiState,
    onBack: () -> Unit,
    onRequestChanges: () -> Unit,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Tinjau Rencana Pengerjaan", onBack = onBack)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderCard(state)
                InfoCard()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Milestone proyek", style = PlanReviewTypography.Section, color = TemuKaryaColors.TextHeading)
                    Text("${state.milestones.size} tahap", style = PlanReviewTypography.Count, color = PlanReviewColors.Muted)
                }
                state.milestones.forEachIndexed { index, milestone -> MilestoneRow(index + 1, milestone) }
                TotalCard(state)
                Text(
                    "Progres proyek nanti dihitung otomatis dari bobot milestone yang sudah ditandai selesai.",
                    style = PlanReviewTypography.Legend,
                    color = PlanReviewColors.Muted
                )
            }
            ActionBar(onRequestChanges, onApprove, Modifier.align(Alignment.BottomCenter))
        }
    }
}

/** 270:665: "Langkah sebelum pembayaran", chip status, judul, pengaju & deadline. */
@Composable
private fun HeaderCard(state: PlanReviewUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, CardShape)
            .border(1.dp, PlanReviewColors.Border, CardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Langkah sebelum pembayaran",
                style = PlanReviewTypography.Eyebrow,
                color = PlanReviewColors.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.background(PlanReviewColors.PendingChip, CircleShape).padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_umkm_pending_actions), null, tint = PlanReviewColors.OnPendingChip, modifier = Modifier.size(15.dp))
                Text("Menunggu persetujuanmu", style = PlanReviewTypography.Chip, color = PlanReviewColors.OnPendingChip, maxLines = 1)
            }
        }
        Text(state.projectTitle, style = PlanReviewTypography.Title, color = TemuKaryaColors.TextHeading)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_event), null, tint = PlanReviewColors.Muted, modifier = Modifier.size(17.dp))
            Text(state.submittedBy, style = PlanReviewTypography.Body, color = TemuKaryaColors.TextBody)
        }
    }
}

/** 270:675: catatan hijau tentang dampak persetujuan. */
@Composable
private fun InfoCard() {
    Row(
        modifier = Modifier.fillMaxWidth().background(PlanReviewColors.Info, CardShape).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(painterResource(R.drawable.ic_wallet_info), null, tint = PlanReviewColors.InfoIcon, modifier = Modifier.size(19.dp))
        Text(
            "Periksa tahapan dan bobot yang diusulkan freelancer. Setelah kamu setujui, kesepakatan final terkunci dan proyek masuk tahap Menunggu Pembayaran.",
            style = PlanReviewTypography.Body,
            color = PlanReviewColors.OnInfo,
            modifier = Modifier.weight(1f)
        )
    }
}

/** 270:681: label "Milestone n" / "Bobot (%)" lalu field nama & bobot (read-only). */
@Composable
private fun MilestoneRow(number: Int, milestone: ReviewMilestoneUi) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Milestone $number", style = PlanReviewTypography.Label, color = TemuKaryaColors.TextBody, modifier = Modifier.weight(1f))
            Text("Bobot (%)", style = PlanReviewTypography.Label, color = TemuKaryaColors.TextBody, modifier = Modifier.width(WeightColumn))
            Spacer(Modifier.width(28.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.weight(1f).height(48.dp).background(TemuKaryaColors.SurfaceWhite, FieldShape).padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(milestone.name, style = PlanReviewTypography.Input, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(
                modifier = Modifier.width(WeightColumn).height(48.dp).background(TemuKaryaColors.SurfaceWhite, FieldShape).padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${milestone.weightPercent}", style = PlanReviewTypography.Weight, color = TemuKaryaColors.TextPrimary)
                Text("%", style = PlanReviewTypography.Percent, color = PlanReviewColors.Muted)
            }
        }
    }
}

/** 270:732: total bobot, chip valid, bar segmen berwarna, dan legenda. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TotalCard(state: PlanReviewUiState) {
    Column(
        modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, CardShape).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Total bobot", style = PlanReviewTypography.TotalLabel, color = TemuKaryaColors.TextBody)
                Text("${state.totalWeight}%", style = PlanReviewTypography.Total, color = TemuKaryaColors.TextHeading)
            }
            if (state.totalWeight == 100) {
                Row(
                    modifier = Modifier.background(PlanReviewColors.ValidChip, CircleShape).padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_check_circle_outline), null, tint = PlanReviewColors.OnValidChip, modifier = Modifier.size(15.dp))
                    Text("Total bobot valid", style = PlanReviewTypography.Chip, color = PlanReviewColors.OnValidChip)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(PlanReviewColors.Track),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            state.milestones.forEachIndexed { i, m ->
                if (m.weightPercent > 0) {
                    Box(Modifier.weight(m.weightPercent.toFloat()).fillMaxHeight().background(PlanReviewColors.Segments[i % PlanReviewColors.Segments.size]))
                }
            }
            val rest = 100 - state.totalWeight
            if (rest > 0) Spacer(Modifier.weight(rest.toFloat()))
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            state.milestones.forEachIndexed { i, m ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(PlanReviewColors.Segments[i % PlanReviewColors.Segments.size], CircleShape))
                    Text("${m.name} ${m.weightPercent}%", style = PlanReviewTypography.Legend, color = TemuKaryaColors.TextBody)
                }
            }
        }
    }
}

/** 270:760: Sticky Bottom Bar, "Minta Ubah" (garis) dan "Setujui Rencana" (utama). */
@Composable
private fun ActionBar(onRequestChanges: () -> Unit, onApprove: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PlanReviewColors.BottomBar)
            .drawBehind { drawLine(PlanReviewColors.Border, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx()) }
            .padding(horizontal = 17.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(51.dp)
                .clip(FieldShape)
                .border(1.5.dp, TemuKaryaColors.ActionPrimary, FieldShape)
                .clickable(role = Role.Button, onClick = onRequestChanges),
            contentAlignment = Alignment.Center
        ) {
            Text("Minta Ubah", style = PlanReviewTypography.Button, color = TemuKaryaColors.ActionPrimary)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(51.dp)
                .clip(FieldShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(role = Role.Button, onClick = onApprove),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_profile_check), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(19.dp))
            Text("Setujui Rencana", style = PlanReviewTypography.Button, color = TemuKaryaColors.OnAction)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PlanReviewScreenPreview() {
    PlanReviewScreen(
        state = PlanReviewUiState(
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            submittedBy = "Diajukan Andi Ramadhan, deadline 7 Nov 2026 (14 hari)",
            milestones = listOf(
                ReviewMilestoneUi("Riset & Moodboard Brand", 20),
                ReviewMilestoneUi("Konsep Logo (2 Alternatif)", 30),
                ReviewMilestoneUi("Desain Kemasan Pouch 250gr", 35),
                ReviewMilestoneUi("Mockup & File Siap Cetak", 15)
            )
        ),
        onBack = {}, onRequestChanges = {}, onApprove = {}
    )
}
