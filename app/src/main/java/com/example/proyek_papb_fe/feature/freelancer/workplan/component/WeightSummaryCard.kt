package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val TrackShape = RoundedCornerShape(6.dp)

/**
 * Figma: "Total Bobot" (88:82) — total + chip status, track tersegmen per milestone,
 * dan legenda. Segmen = lebar × bobot, dikurangi celah 2dp (sesuai 88:91–88:94).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WeightSummaryCard(
    milestones: List<MilestoneInput>,
    validation: WorkPlanValidation,
    isSubmitted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceContainer, WorkPlanCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Total bobot",
                    style = WorkPlanTypography.TotalLabel,
                    color = TemuKaryaColors.TextBody
                )
                Text(
                    text = "${validation.totalWeight}%",
                    style = WorkPlanTypography.TotalValue,
                    color = if (validation.totalWeight > 100) WorkPlanColors.Error else TemuKaryaColors.TextHeading
                )
            }
            TotalStatusChip(validation = validation, isSubmitted = isSubmitted)
        }

        WeightTrack(milestones = milestones)

        if (milestones.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                milestones.forEachIndexed { index, milestone ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(WorkPlanColors.segment(index), CircleShape)
                        )
                        Text(
                            text = "${milestone.legendLabel(index)} ${milestone.weightValue}%",
                            style = WorkPlanTypography.Legend,
                            color = TemuKaryaColors.TextBody
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalStatusChip(validation: WorkPlanValidation, isSubmitted: Boolean) {
    when {
        isSubmitted -> WorkPlanChip(
            iconRes = R.drawable.ic_check_circle_outline,
            label = "Sudah diajukan",
            container = WorkPlanColors.ReadyChip,
            content = WorkPlanColors.OnReadyChip
        )
        validation.isReady -> WorkPlanChip(
            iconRes = R.drawable.ic_check_circle_outline,
            label = "Siap diajukan",
            container = WorkPlanColors.ReadyChip,
            content = WorkPlanColors.OnReadyChip
        )
        else -> {
            val total = validation.totalWeight
            WorkPlanChip(
                iconRes = R.drawable.ic_warning,
                label = when {
                    total < 100 -> "Kurang ${100 - total}%"
                    total > 100 -> "Lebih ${total - 100}%"
                    else -> "Belum lengkap"
                },
                container = WorkPlanColors.PendingChip,
                content = WorkPlanColors.OnPendingChip
            )
        }
    }
}

@Composable
private fun WeightTrack(milestones: List<MilestoneInput>) {
    val total = milestones.sumOf { it.weightValue }
    // Jika total > 100%, segmen diskalakan agar tetap muat di track.
    val scale = if (total > 100) 100f / total else 1f
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(TrackShape)
            .background(WorkPlanColors.Track)
    ) {
        val gap = 2.dp.toPx()
        var start = 0f
        milestones.forEachIndexed { index, milestone ->
            val span = size.width * milestone.weightValue * scale / 100f
            val width = span - gap
            if (width > 0f) {
                drawRect(
                    color = WorkPlanColors.segment(index),
                    topLeft = Offset(start, 0f),
                    size = Size(width, size.height)
                )
            }
            start += span
        }
    }
}
