package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val RecommendationCardWidth = 295.795.dp

/**
 * Figma: Article - Recommendation Card (17:3859 / 17:3895), versi ringkas: tinggi mengikuti isi
 * (gambar → judul → deskripsi → budget) tanpa ruang kosong. Judul & deskripsi memakai
 * minLines = maxLines = 2 agar semua kartu di carousel sama tinggi.
 */
@Composable
internal fun RecommendationCard(
    project: DashboardProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .width(RecommendationCardWidth)
            .dashboardCardShadow(shape)
            .clip(shape)
            .background(DashboardColors.SurfaceCard)
            .clickable(onClick = onClick)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.221.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DashboardColors.SurfaceImage)
        ) {
            if (project.imageRes != null) {
                Image(
                    painter = painterResource(project.imageRes),
                    contentDescription = project.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = project.imageEmoji,
                    fontSize = 48.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            project.matchPercentage?.let { percentage ->
                MatchBadge(
                    text = "$percentage% Cocok",
                    modifier = Modifier.padding(start = 8.45.dp, top = 8.45.dp)
                )
            }
        }

        Text(
            text = project.title,
            style = DashboardTypography.CardTitle,
            color = DashboardColors.TextHeading,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.226.dp)
        )

        Text(
            text = project.description,
            style = DashboardTypography.BodyRegular,
            color = DashboardColors.TextBody,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Budget langsung di bawah deskripsi (jarak total 8.451 = spacedBy + padding).
        Text(
            text = project.budget,
            style = DashboardTypography.CardTitle,
            color = DashboardColors.TextHeading,
            modifier = Modifier.padding(top = 4.226.dp)
        )
    }
}

@Composable
private fun MatchBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(DashboardColors.Olive)
            .padding(horizontal = 8.451.dp, vertical = 2.113.dp)
    ) {
        Text(
            text = text,
            style = DashboardTypography.Badge,
            color = Color.White
        )
    }
}
