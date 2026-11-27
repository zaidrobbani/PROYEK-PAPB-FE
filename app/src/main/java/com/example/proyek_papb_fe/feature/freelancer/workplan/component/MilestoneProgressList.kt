package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

data class MilestoneProgressItem(
    val id: String,
    val name: String,
    val weightPercent: Int,
    val isDone: Boolean,
    val completedLabel: String?
)

private val CardShape = RoundedCornerShape(16.dp)

@Composable
internal fun MilestoneProgressList(
    items: List<MilestoneProgressItem>,
    progressPercent: Int,
    canComplete: Boolean,
    isBusy: Boolean,
    onComplete: (MilestoneProgressItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(TemuKaryaColors.SurfaceWhite)
                .border(1.dp, WorkPlanColors.Outline, CardShape)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Progres pengerjaan", style = WorkPlanTypography.FieldLabel, color = TemuKaryaColors.TextBody, modifier = Modifier.weight(1f))
                Text("$progressPercent%", style = WorkPlanTypography.CardTitle, color = TemuKaryaColors.TextHeading)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(WorkPlanColors.Track)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercent.coerceIn(0, 100) / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(TemuKaryaColors.Olive)
                )
            }
            Text(
                "${items.count { it.isDone }} dari ${items.size} milestone selesai",
                style = WorkPlanTypography.Meta,
                color = TemuKaryaColors.TextPlaceholder
            )
        }

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(if (item.isDone) WorkPlanColors.InfoCard else TemuKaryaColors.SurfaceWhite)
                    .border(1.dp, WorkPlanColors.Outline, CardShape)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(if (item.isDone) TemuKaryaColors.Olive else TemuKaryaColors.SurfaceSand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isDone) {
                        Icon(painterResource(R.drawable.ic_symbol_check), "Selesai", tint = TemuKaryaColors.OnAction, modifier = Modifier.size(16.dp))
                    } else {
                        Text("${index + 1}", style = WorkPlanTypography.Count, color = TemuKaryaColors.ActionPrimary)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.name, style = WorkPlanTypography.FieldLabel, color = TemuKaryaColors.TextPrimary)
                    Text(
                        text = "Bobot ${item.weightPercent}% · " + (item.completedLabel?.let { "Selesai $it" } ?: "Belum selesai"),
                        style = WorkPlanTypography.Meta,
                        color = if (item.isDone) WorkPlanColors.OnInfoCard else TemuKaryaColors.TextPlaceholder
                    )
                }
                if (!item.isDone && canComplete) {
                    Text(
                        text = "Tandai Selesai",
                        style = WorkPlanTypography.Chip,
                        color = TemuKaryaColors.OnAction,
                        modifier = Modifier
                            .alpha(if (isBusy) 0.5f else 1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TemuKaryaColors.ActionPrimary)
                            .clickable(enabled = !isBusy, role = Role.Button, onClickLabel = "Tandai ${item.name} selesai") { onComplete(item) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
