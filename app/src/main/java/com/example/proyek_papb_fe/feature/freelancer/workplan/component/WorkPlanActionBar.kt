package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val ButtonHeight = 51.dp
private const val DisabledAlpha = 0.38f

/** Figma: "Button Tambah Milestone" (88:79) — garis putus-putus 1.2dp, radius 12. */
@Composable
internal fun AddMilestoneButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight)
            .alpha(if (enabled) 1f else DisabledAlpha)
            .clip(WorkPlanFieldShape)
            .background(WorkPlanColors.AddButton)
            .drawBehind {
                val stroke = 1.2.dp.toPx()
                val radius = 12.dp.toPx() - stroke / 2
                drawRoundRect(
                    color = WorkPlanColors.AddButtonBorder,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = size.copy(width = size.width - stroke, height = size.height - stroke),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
                    )
                )
            }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            tint = TemuKaryaColors.ActionPrimary,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = if (enabled) "Tambah Milestone" else "Maksimal $MaxMilestones milestone",
            style = WorkPlanTypography.Button,
            color = TemuKaryaColors.ActionPrimary,
            maxLines = 1
        )
    }
}

/** Figma: Sticky Bottom Bar (88:114) — "Simpan Draf" (outline) + "Ajukan ke UMKM" (primary). */
@Composable
internal fun WorkPlanActionBar(
    isSubmitted: Boolean,
    onSaveDraftClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WorkPlanColors.BottomBarScrim)
            .drawBehind {
                drawLine(
                    color = WorkPlanColors.Outline,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 17.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .alpha(if (isSubmitted) DisabledAlpha else 1f)
                .clip(WorkPlanFieldShape)
                .border(1.5.dp, TemuKaryaColors.ActionPrimary, WorkPlanFieldShape)
                .clickable(enabled = !isSubmitted, onClick = onSaveDraftClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Simpan Draf",
                style = WorkPlanTypography.Button,
                color = TemuKaryaColors.ActionPrimary,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .alpha(if (isSubmitted) DisabledAlpha else 1f)
                .clip(WorkPlanFieldShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(enabled = !isSubmitted, onClick = onSubmitClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(if (isSubmitted) R.drawable.ic_check_circle_outline else R.drawable.ic_send),
                contentDescription = null,
                tint = TemuKaryaColors.OnAction,
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = if (isSubmitted) "Diajukan" else "Ajukan ke UMKM",
                style = WorkPlanTypography.Button,
                color = TemuKaryaColors.OnAction,
                maxLines = 1
            )
        }
    }
}
