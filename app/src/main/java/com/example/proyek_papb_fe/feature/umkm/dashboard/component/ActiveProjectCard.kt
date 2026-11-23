package com.example.proyek_papb_fe.feature.umkm.dashboard.component

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.core.ui.components.InitialsAvatar
import com.example.proyek_papb_fe.core.ui.components.ProfilePhoto
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardActiveProject

@Composable
fun ActiveProjectCard(
    project: DashboardActiveProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.903.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = project.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.113.dp),
                color = colors.onSurface,
                style = typography.titleSmall.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
            Text(
                text = project.statusLabel,
                modifier = Modifier
                    .background(SageBadge, CircleShape)
                    .padding(horizontal = 10.564.dp, vertical = 4.226.dp),
                color = OnSageBadge,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 12.677.dp)
                .fillMaxWidth()
                .background(colors.surfaceContainerLowest, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.677.dp, vertical = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val photo = project.freelancerPhoto
                if (photo != null) {
                    Image(
                        painter = painterResource(photo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(29.579.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ProfilePhoto(
                        photoUrl = project.freelancerPhotoUrl,
                        name = project.freelancerName,
                        modifier = Modifier.size(29.579.dp),
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = project.freelancerName,
                    color = colors.onSurface,
                    style = typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.621.sp,
                        lineHeight = 14.79.sp
                    )
                )
            }
            Text(
                text = project.budget,
                color = colors.onTertiaryContainer,
                style = typography.labelMedium.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 12.677.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progres Pengerjaan",
                color = colors.onSurfaceVariant,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
            Text(
                text = "${(project.progress * 100).toInt()}%",
                color = colors.onTertiaryContainer,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 6.338.dp)
                .fillMaxWidth()
                .height(10.564.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(project.progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(colors.secondary)
            )
        }
    }
}
