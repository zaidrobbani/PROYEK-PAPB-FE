package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.AvatarTone
import com.example.proyek_papb_fe.core.data.model.MatchLevel
import com.example.proyek_papb_fe.core.data.model.ProjectMatch
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Bayangan Figma default: 0 4.226 16.903 -2.113 dengan warna [color]. */
internal fun Modifier.searchProjectShadow(
    shape: Shape,
    color: Color,
    radius: Dp = 16.903.dp,
    spread: Dp = (-2.113).dp,
    offsetY: Dp = 4.226.dp
): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = radius,
        spread = spread,
        color = color,
        offset = DpOffset(0.dp, offsetY)
    )
)

/**
 * Figma: Article - Card (2011:288 / 2011:323 / 2011:358 / 2011:393).
 * Kartu sorotan (punya [SearchProject.match]) memakai latar #FBF3E0 + bayangan;
 * kartu biasa transparan dengan tag kategori.
 */
@Composable
internal fun ProjectFeedCard(
    project: SearchProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (project.isHighlighted) {
                    Modifier
                        .searchProjectShadow(shape, TemuKaryaColors.ShadowCard)
                        .clip(shape)
                        .background(TemuKaryaColors.SurfaceCard)
                } else {
                    Modifier.clip(shape)
                }
            )
            .clickable(onClick = onClick)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopStrip(project)

        Column(verticalArrangement = Arrangement.spacedBy(4.226.dp)) {
            Text(
                text = project.title,
                style = TemuKaryaTypography.CardTitle,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            ClientRow(project)
        }

        KeyMetricsBox(project)
    }
}

@Composable
private fun TopStrip(project: SearchProject) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val match = project.match
        if (match != null) {
            MatchBadge(match)
        } else {
            CategoryTag(project.tagLabel)
        }
        if (project.isNew) {
            Box(
                modifier = Modifier
                    .size(6.338.dp)
                    .clip(CircleShape)
                    .background(TemuKaryaColors.Olive)
            )
        }
    }
}

@Composable
private fun MatchBadge(match: ProjectMatch) {
    val isTop = match.level == MatchLevel.TOP
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isTop) TemuKaryaColors.SurfaceSand else TemuKaryaColors.ChipGreen)
            .padding(horizontal = 10.564.dp, vertical = 2.113.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(if (isTop) R.drawable.ic_match_spark else R.drawable.ic_thumb_up),
            contentDescription = null
        )
        Text(
            text = if (isTop) "${match.percentage}% Cocok untuk Anda" else "${match.percentage}% Cocok",
            style = if (isTop) TemuKaryaTypography.CaptionBold else TemuKaryaTypography.CaptionSemiBold,
            color = if (isTop) TemuKaryaColors.TextStrong else TemuKaryaColors.OnChipGreen,
            maxLines = 1
        )
    }
}

@Composable
private fun CategoryTag(text: String) {
    Text(
        text = text,
        style = TemuKaryaTypography.CaptionMedium,
        color = TemuKaryaColors.TextBody,
        maxLines = 1,
        modifier = Modifier
            .clip(CircleShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .padding(horizontal = 10.564.dp, vertical = 2.113.dp)
    )
}

@Composable
private fun ClientRow(project: SearchProject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.113.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClientAvatar(initials = project.clientInitials, tone = project.avatarTone)

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = project.clientName,
                style = TemuKaryaTypography.LabelMedium,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            // Figma: elemen teks kosong di antara nama & lokasi menambah jarak 2 x 8.451.
            Row(
                modifier = Modifier.padding(start = 16.903.dp),
                horizontalArrangement = Arrangement.spacedBy(2.102.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_location_pin),
                    contentDescription = "Lokasi"
                )
                Text(
                    text = project.clientLocation,
                    style = TemuKaryaTypography.BodyRegular,
                    color = TemuKaryaColors.TextBody,
                    maxLines = 1
                )
            }
        }

        if (project.isClientVerified) {
            Image(
                painter = painterResource(R.drawable.ic_verified),
                contentDescription = "Klien terverifikasi"
            )
        }
    }
}

@Composable
private fun ClientAvatar(initials: String, tone: AvatarTone) {
    val (background, content) = when (tone) {
        AvatarTone.GREEN -> TemuKaryaColors.ChipGreen to TemuKaryaColors.OnChipGreen
        AvatarTone.SAND -> TemuKaryaColors.SurfaceSand to TemuKaryaColors.TextHeading
    }
    Box(
        modifier = Modifier
            .size(25.354.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = TemuKaryaTypography.Avatar,
            color = content,
            maxLines = 1
        )
    }
}

/** Figma: Key Metrics Box (2011:311). */
@Composable
private fun KeyMetricsBox(project: SearchProject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TemuKaryaColors.SurfaceContainer)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = project.budgetLabel,
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextBody
            )
            Text(
                text = project.budget,
                style = TemuKaryaTypography.Amount,
                color = TemuKaryaColors.TextHeading,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .width(1.056.dp)
                .height(25.354.dp)
                .background(TemuKaryaColors.MetricDivider)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = project.deadlineLabel,
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextBody
            )
            Text(
                text = project.deadline,
                style = TemuKaryaTypography.LabelSemiBold,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1
            )
        }
    }
}
