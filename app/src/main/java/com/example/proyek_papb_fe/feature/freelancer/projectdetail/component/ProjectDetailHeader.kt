package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.MatchLevel
import com.example.proyek_papb_fe.core.data.model.ProjectMatch
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Project Header & Badges (17:4212). */
@Composable
internal fun ProjectDetailHeader(
    title: String,
    publishedAgo: String,
    match: ProjectMatch?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        if (match != null) {
            DetailMatchBadge(match)
        }

        Text(
            text = title,
            style = TemuKaryaTypography.CardTitle,
            color = TemuKaryaColors.TextHeading,
            modifier = Modifier.padding(top = 4.226.dp)
        )

        Row(
            modifier = Modifier.padding(top = 2.113.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_schedule),
                contentDescription = null
            )
            Text(
                text = publishedAgo,
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextBody
            )
        }
    }
}

/** Badge versi detail: selalu hijau + ikon sparkle + SemiBold (berbeda dari listing). */
@Composable
private fun DetailMatchBadge(match: ProjectMatch) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(TemuKaryaColors.ChipGreen)
            .padding(horizontal = 12.677.dp, vertical = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_sparkle),
            contentDescription = null
        )
        Text(
            text = if (match.level == MatchLevel.TOP) {
                "${match.percentage}% Cocok untuk Anda"
            } else {
                "${match.percentage}% Cocok"
            },
            style = TemuKaryaTypography.CaptionSemiBold,
            color = TemuKaryaColors.OnChipGreen,
            maxLines = 1
        )
    }
}
