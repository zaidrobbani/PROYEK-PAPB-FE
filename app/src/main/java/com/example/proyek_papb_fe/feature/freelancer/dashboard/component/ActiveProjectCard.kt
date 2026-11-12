package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R

/** Bayangan kartu Figma: 0 4.226 16.903 -2.113 rgba(104,71,56,0.08). */
internal fun Modifier.dashboardCardShadow(shape: Shape): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 16.903.dp,
        spread = (-2.113).dp,
        color = DashboardColors.CardShadow,
        offset = DpOffset(0.dp, 4.226.dp)
    )
)

/** Figma: Article - Active Project Card (17:3774). */
@Composable
internal fun ActiveProjectCard(
    project: DashboardProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dashboardCardShadow(shape)
            .clip(shape)
            .background(DashboardColors.SurfaceCard)
            .clickable(onClick = onClick)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.226.dp))  {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.338.dp)
            ) {
                StatusChip(text = project.status)
                project.daysRemaining?.let { days ->
                    Text(
                        text = "Sisa $days hari",
                        style = DashboardTypography.CaptionMedium,
                        color = DashboardColors.Danger
                    )
                }
            }

            Text(
                text = project.title,
                style = DashboardTypography.ActiveTitle,
                color = DashboardColors.TextHeading,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (project.owner.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_storefront),
                        contentDescription = null
                    )
                    Text(
                        text = project.owner,
                        style = DashboardTypography.BodyRegular,
                        color = DashboardColors.TextBody,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        MilestoneProgress(progress = project.progress)
    }
}

@Composable
private fun StatusChip(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(DashboardColors.ChipBackground)
            .padding(horizontal = 10.564.dp, vertical = 2.113.dp)
    ) {
        Text(
            text = text,
            style = DashboardTypography.CaptionSemiBold,
            color = DashboardColors.ChipText
        )
    }
}

@Composable
private fun MilestoneProgress(progress: Int) {
    val clamped = progress.coerceIn(0, 100)
    Column(verticalArrangement = Arrangement.spacedBy(6.338.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progres milestone",
                style = DashboardTypography.CaptionMedium,
                color = DashboardColors.TextBody
            )
            Text(
                text = "$clamped%",
                style = DashboardTypography.CaptionBold,
                color = DashboardColors.TextHeading
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.451.dp)
                .clip(CircleShape)
                .background(DashboardColors.ProgressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped / 100f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(DashboardColors.ActionPrimary)
            )
        }
    }
}
