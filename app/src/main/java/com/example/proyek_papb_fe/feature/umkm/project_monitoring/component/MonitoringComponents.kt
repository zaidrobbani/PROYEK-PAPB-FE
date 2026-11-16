package com.example.proyek_papb_fe.feature.umkm.project_monitoring.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnVerifiedChip
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.core.ui.theme.VerifiedChip
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.DelayRisk
import com.example.proyek_papb_fe.feature.umkm.data.ProjectMilestone
import com.example.proyek_papb_fe.feature.umkm.data.ProjectMonitoringResult
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.utcMillisToDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

// Warna chip Figma frame ini yang belum ada di tema; disimpan lokal agar tema bersama tidak berubah.
private val WorkingChip = Color(0xFFAAB295)
private val OnWorkingChip = Color(0xFF2A351E)
private val RiskText = Color(0xFF93000A)
private val TargetFill = Color(0x1FBA1A1A) // rgba(186,26,26,0.12)

private val IndonesianLocale: Locale = Locale.forLanguageTag("id-ID")
private val ShortDate = DateTimeFormatter.ofPattern("d MMM", IndonesianLocale)
private val DateTime = DateTimeFormatter.ofPattern("d MMM, HH.mm", IndonesianLocale)

fun formatMonitoringDate(date: LocalDate): String = date.format(ShortDate)

/** Figma memakai line-height 1.4 untuk teks di screen ini. */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

@Composable
private fun StatusChip(@DrawableRes icon: Int?, label: String, background: Color, content: Color) {
    Row(
        modifier = Modifier
            .background(background, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(painterResource(icon), contentDescription = null, tint = content, modifier = Modifier.size(15.dp))
        }
        Text(text = label, color = content, style = text(FontWeight.SemiBold, 11.6f))
    }
}

/** Kartu judul: chip status project + chip risiko, judul, dan freelancer. */
@Composable
fun MonitoringHeaderCard(
    title: String,
    status: ProjectStatus,
    risk: DelayRisk?,
    freelancerName: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .border(1.dp, colors.outlineVariant, shape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                icon = if (status == ProjectStatus.SedangDikerjakan) R.drawable.ic_symbol_brush else null,
                label = status.label,
                background = WorkingChip,
                content = OnWorkingChip
            )
            if (risk != null) RiskChip(risk)
        }
        Text(text = title, color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 16.9f))
        if (freelancerName != null) {
            Text(text = "$freelancerName, Freelancer", color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 12.7f))
        }
    }
}

@Composable
private fun RiskChip(risk: DelayRisk) {
    // Figma hanya menggambar "Berisiko Terlambat"; kategori lain memakai warna tema yang senada.
    val (background, content) = when (risk) {
        DelayRisk.SesuaiTarget -> SageBadge to MaterialTheme.colorScheme.onSecondaryContainer
        DelayRisk.PerluPerhatian -> SandContainer to MaterialTheme.colorScheme.onTertiaryContainer
        DelayRisk.BerisikoTerlambat, DelayRisk.Terlambat -> MaterialTheme.colorScheme.errorContainer to RiskText
    }
    StatusChip(
        icon = if (risk == DelayRisk.SesuaiTarget) null else R.drawable.ic_symbol_warning,
        label = risk.label,
        background = background,
        content = content
    )
}

/** Kartu progres: aktual vs target, bar dengan penanda target, dan 3 ringkasan. */
@Composable
fun MonitoringProgressCard(result: ProjectMonitoringResult, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.tertiaryContainer, RoundedCornerShape(16.dp))
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Progres aktual", color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 12.7f))
                Text("${result.actualPercent}%", color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 33.8f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Target hari ini", color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 12.7f))
                Text("${result.targetPercent}%", color = colors.outline, style = text(FontWeight.Bold, 21.1f))
            }
        }
        ProgressBarWithTarget(actual = result.actualPercent, target = result.targetPercent)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val behind = result.gapPoints > 0
            StatBox(
                value = "${abs(result.gapPoints)} poin",
                label = if (behind) "Tertinggal" else if (result.gapPoints < 0) "Lebih cepat" else "Sesuai target",
                valueColor = if (behind && result.risk != DelayRisk.SesuaiTarget) colors.error else colors.onTertiaryContainer
            )
            StatBox(
                value = "${abs(result.remainingDays)} hari",
                label = if (result.remainingDays >= 0) "Sisa waktu" else "Lewat deadline",
                valueColor = if (result.remainingDays >= 0) colors.onTertiaryContainer else colors.error
            )
            StatBox(
                value = "${result.completedMilestones} dari ${result.totalMilestones}",
                label = "Milestone selesai",
                valueColor = colors.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.StatBox(value: String, label: String, valueColor: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = valueColor, maxLines = 1, style = text(FontWeight.Bold, 15.8f))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, style = text(FontWeight.Normal, 10.6f))
    }
}

@Composable
private fun ProgressBarWithTarget(actual: Int, target: Int) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(14.dp)) {
            val width = maxWidth
            Box(Modifier.fillMaxWidth().fillMaxHeight().background(colors.surfaceVariant, RoundedCornerShape(7.dp)))
            Box(Modifier.width(width * (target / 100f)).fillMaxHeight().background(TargetFill, RoundedCornerShape(7.dp)))
            Box(Modifier.width(width * (actual / 100f)).fillMaxHeight().background(colors.primary, RoundedCornerShape(7.dp)))
            // Penanda target: garis 2 x 22, menonjol 4dp di atas dan bawah track.
            Box(
                Modifier
                    .offset(x = (width * (target / 100f) - 1.dp).coerceIn(0.dp, width - 2.dp))
                    // requiredSize: tetap 22dp walau track hanya 14dp, terpusat secara vertikal.
                    .requiredSize(width = 2.dp, height = 22.dp)
                    .background(colors.primary)
            )
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
            var labelWidth by remember { mutableIntStateOf(0) }
            val small = text(FontWeight.Medium, 10.6f)
            Text("0%", color = colors.outline, style = small, modifier = Modifier.align(Alignment.TopStart))
            Text("100%", color = colors.outline, style = small, modifier = Modifier.align(Alignment.TopEnd))
            Text(
                text = "Target $target%",
                color = colors.onTertiaryContainer,
                style = text(FontWeight.Bold, 10.6f),
                modifier = Modifier
                    .onSizeChanged { labelWidth = it.width }
                    .offset {
                        val x = (widthPx * target / 100f - labelWidth / 2f).coerceIn(0f, widthPx - labelWidth)
                        IntOffset(x.roundToInt(), 0)
                    }
            )
        }
    }
}

/** Peringatan risiko; teks mengikuti contoh Figma dan dibangun dari hasil perhitungan. */
@Composable
fun MonitoringRiskAlert(result: ProjectMonitoringResult, nextMilestone: ProjectMilestone?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val risk = result.risk ?: return
    if (risk == DelayRisk.SesuaiTarget) return
    val ask = nextMilestone?.let {
        " Kamu bisa menanyakan rencana penyelesaian ${it.name.substringBefore(" (")} (${it.weightPercent}%) ke freelancer lewat chat."
    } ?: ""
    val body = if (risk == DelayRisk.Terlambat) {
        "Deadline ${formatMonitoringDate(result.deadline)} sudah terlewati, progres baru ${result.actualPercent}%.$ask"
    } else {
        "Hari ke-${result.elapsedDays} dari ${result.totalDays}, target progres ${result.targetPercent}% " +
            "tapi baru ${result.actualPercent}% yang selesai.$ask"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.errorContainer, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(painterResource(R.drawable.ic_symbol_error), contentDescription = null, tint = colors.error, modifier = Modifier.size(23.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(risk.label.lowercase().replaceFirstChar { it.uppercase() }, color = RiskText, style = text(FontWeight.Bold, 14.8f))
            Text(body, color = colors.onErrorContainer, style = text(FontWeight.Normal, 12.7f))
        }
    }
}

/** Linimasa Mulai / Hari ini / Deadline. */
@Composable
fun MonitoringTimeline(result: ProjectMonitoringResult, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerLow, RoundedCornerShape(16.dp))
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TimelineLabel("Mulai", formatMonitoringDate(result.startDate), colors.onSurface, Alignment.Start)
            TimelineLabel("Hari ini", formatMonitoringDate(result.evaluationDate), colors.primary, Alignment.CenterHorizontally)
            TimelineLabel("Deadline", formatMonitoringDate(result.deadline), colors.onSurface, Alignment.End)
        }
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(colors.surfaceVariant)) {
            Box(
                Modifier.fillMaxWidth(result.timeFraction).fillMaxHeight()
                    .background(colors.primary, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
private fun TimelineLabel(label: String, value: String, valueColor: Color, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = MaterialTheme.colorScheme.outline, style = text(FontWeight.Medium, 11.6f))
        Text(value, color = valueColor, style = text(FontWeight.Bold, 13.7f))
    }
}

@Composable
fun MilestoneSectionHeader(agreedDate: LocalDate?, onOpenPlan: (() -> Unit)?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Milestone pengerjaan", color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 15.8f))
        if (agreedDate != null) {
            // Tambahan: label kesepakatan bisa diketuk untuk melihat rencana pengerjaan yang disetujui.
            Row(
                modifier = if (onOpenPlan != null) {
                    Modifier.clip(RoundedCornerShape(8.dp)).clickable(role = Role.Button, onClickLabel = "Lihat rencana pengerjaan", onClick = onOpenPlan)
                } else Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Kesepakatan final ${formatMonitoringDate(agreedDate)}",
                    color = colors.outline,
                    style = text(FontWeight.Medium, 11.6f)
                )
                if (onOpenPlan != null) {
                    Icon(painterResource(R.drawable.ic_symbol_chevron_right), null, tint = colors.outline, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * Daftar milestone, hanya baca. Status Blueprint cuma BELUM_SELESAI/SELESAI; milestone
 * belum selesai yang pertama ditandai "Sedang dikerjakan" sebagai tahapan berjalan.
 */
@Composable
fun MilestoneList(milestones: List<ProjectMilestone>, inProgress: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val currentId = if (inProgress) milestones.firstOrNull { !it.isDone }?.id else null
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerLow, RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        milestones.forEachIndexed { index, milestone ->
            if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
            val current = milestone.id == currentId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (current) colors.tertiaryContainer else Color.Transparent)
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        if (milestone.isDone) R.drawable.ic_symbol_check_circle else R.drawable.ic_symbol_radio_button_unchecked
                    ),
                    contentDescription = if (milestone.isDone) "Selesai" else "Belum selesai",
                    tint = if (milestone.isDone) colors.secondary else colors.outline,
                    modifier = Modifier.size(25.dp)
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = milestone.name,
                        color = if (milestone.isDone) colors.onSurfaceVariant else colors.onSurface,
                        style = text(if (milestone.isDone) FontWeight.Medium else FontWeight.SemiBold, 13.7f)
                    )
                    Text(
                        text = milestone.completedUtcMillis
                            ?.let { "Selesai ${utcMillisToDateTime(it).format(DateTime)} WIB" }
                            ?: if (current) "Sedang dikerjakan" else "Belum dimulai",
                        color = if (milestone.isDone) colors.secondary else colors.outline,
                        style = text(FontWeight.Normal, 11.6f)
                    )
                }
                Text(
                    text = "${milestone.weightPercent}%",
                    modifier = Modifier
                        .background(if (milestone.isDone) VerifiedChip else colors.surfaceVariant, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    color = if (milestone.isDone) OnVerifiedChip else colors.onSurfaceVariant,
                    style = text(FontWeight.SemiBold, 11.6f)
                )
            }
        }
    }
}

@Composable
fun MonitoringInfoNote(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SageBadge.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(painterResource(R.drawable.ic_symbol_info), contentDescription = null, tint = colors.secondary, modifier = Modifier.size(19.dp))
        Text(
            text = "Progres dihitung otomatis dari milestone yang ditandai selesai oleh freelancer. " +
                "Kamu bisa memantau, tapi tidak mengubah status milestone.",
            modifier = Modifier.weight(1f),
            color = colors.onSecondaryContainer,
            style = text(FontWeight.Normal, 12.7f)
        )
    }
}

@Composable
fun MonitoringUpdatedAt(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline,
        style = text(FontWeight.Normal, 11.6f)
    )
}

/** Bar bawah: tombol garis "Chat Freelancer". */
@Composable
fun MonitoringBottomBar(onChat: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Column(modifier = modifier.fillMaxWidth().background(colors.background.copy(alpha = 0.97f))) {
        HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
        Row(
            modifier = Modifier
                .padding(horizontal = 17.dp, vertical = 14.dp)
                .fillMaxWidth()
                .height(51.dp)
                .clip(shape)
                .border(1.5.dp, colors.primary, shape)
                .clickable(role = Role.Button, onClick = onChat),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_symbol_chat), contentDescription = null, tint = colors.primary, modifier = Modifier.size(19.dp))
            Text("Chat Freelancer", color = colors.primary, style = text(FontWeight.SemiBold, 14.8f))
        }
    }
}
