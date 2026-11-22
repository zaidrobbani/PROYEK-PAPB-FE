package com.example.proyek_papb_fe.feature.umkm.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.core.ui.components.InitialsAvatar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardFreelancer

private fun Modifier.photoRing(color: Color): Modifier = drawBehind {
    val spread = 2.113.dp.toPx()
    val radius = 12.dp.toPx() + spread
    drawRoundRect(
        color = color,
        topLeft = Offset(-spread, -spread),
        size = Size(size.width + spread * 2, size.height + spread * 2),
        cornerRadius = CornerRadius(radius, radius)
    )
}

@Composable
fun FreelancerRecommendationCard(
    freelancer: DashboardFreelancer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(16.dp)
    val photoShape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .width(304.246.dp)
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.903.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.677.dp)) {
            val photo = freelancer.photo
            if (photo != null) {
                Image(
                    painter = painterResource(photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.708.dp)
                        .photoRing(colors.primary.copy(alpha = 0.2f))
                        .clip(photoShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                InitialsAvatar(
                    name = freelancer.name,
                    modifier = Modifier
                        .size(50.708.dp)
                        .photoRing(colors.primary.copy(alpha = 0.2f)),
                    shape = photoShape,
                    fontSize = 16.sp
                )
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = freelancer.name,
                        color = colors.onSurface,
                        style = typography.titleSmall.withFrameMetrics(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.79.sp,
                            lineHeight = 21.128.sp
                        )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_register_verified),
                        contentDescription = "Terverifikasi"
                    )
                }
                Text(
                    text = freelancer.role,
                    color = colors.onSurfaceVariant,
                    style = typography.bodySmall.withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.677.sp,
                        lineHeight = 16.903.sp
                    )
                )
                Row(
                    modifier = Modifier.padding(top = 4.226.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_dashboard_star),
                            contentDescription = null
                        )
                        Text(
                            text = freelancer.rating,
                            modifier = Modifier.padding(start = 2.113.dp),
                            color = colors.onSurface,
                            style = typography.labelSmall.withFrameMetrics(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.621.sp,
                                lineHeight = 14.79.sp
                            )
                        )
                    }
                    Text(
                        text = freelancer.reviewLabel,
                        color = colors.outline,
                        style = typography.bodySmall.withFrameMetrics(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.677.sp,
                            lineHeight = 16.903.sp
                        )
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(top = 12.677.dp + 4.226.dp)) {
            Text(
                text = "Mulai dari",
                color = colors.outline,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
            Text(
                text = freelancer.startingPrice,
                color = colors.onTertiaryContainer,
                style = typography.titleSmall.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
        }
    }
}
