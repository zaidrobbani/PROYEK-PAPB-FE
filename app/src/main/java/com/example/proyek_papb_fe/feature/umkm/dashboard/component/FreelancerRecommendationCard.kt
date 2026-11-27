package com.example.proyek_papb_fe.feature.umkm.dashboard.component

import com.example.proyek_papb_fe.feature.umkm.component.shared.FreelancerMatchBadge
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
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardFreelancer
import com.example.proyek_papb_fe.core.ui.components.ProfilePhoto

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
                ProfilePhoto(
                            photoUrl = freelancer.photoUrl,
                            name = freelancer.name,
                    modifier = Modifier
                        .size(50.708.dp)
                        .photoRing(colors.primary.copy(alpha = 0.2f)),
                    shape = photoShape,
                    fontSize = 16.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = freelancer.name,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onSurfaceVariant,
                    style = typography.bodySmall.withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.677.sp,
                        lineHeight = 16.903.sp
                    )
                )
                freelancer.matchPercent?.let {
                    FreelancerMatchBadge(it, freelancer.matchProjectTitle, Modifier.padding(top = 4.226.dp))
                }
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
            AvailabilityPill(freelancer.isAvailable)
        }

        Row(
            modifier = Modifier.padding(top = 12.677.dp),
            horizontalArrangement = Arrangement.spacedBy(12.677.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (freelancer.city.isNotBlank()) {
                InfoItem(R.drawable.ic_find_location, freelancer.city)
            }
            InfoItem(
                R.drawable.ic_find_experience,
                if (freelancer.experienceCount > 0) "${freelancer.experienceCount} pengalaman" else "Belum ada pengalaman"
            )
        }

        Row(
            modifier = Modifier.padding(top = 8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (freelancer.skills.isEmpty()) {
                SkillChip("Keahlian belum diisi", muted = true)
            } else {
                freelancer.skills.take(2).forEach { SkillChip(it, modifier = Modifier.weight(1f, fill = false)) }
                if (freelancer.skills.size > 2) SkillChip("+${freelancer.skills.size - 2}", muted = true)
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 12.677.dp),
            color = colors.outlineVariant.copy(alpha = 0.5f)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.564.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = freelancer.priceLabel,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typography.titleSmall.withFrameMetrics(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.79.sp,
                        lineHeight = 21.128.sp
                    )
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colors.primary)
                    .padding(start = 12.677.dp, end = 8.451.dp, top = 6.338.dp, bottom = 6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat Profil",
                    color = colors.onPrimary,
                    style = typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AvailabilityPill(isAvailable: Boolean) {
    val colors = MaterialTheme.colorScheme
    val (background, content, label) = if (isAvailable) {
        Triple(SageBadge, OnSageBadge, "Tersedia")
    } else {
        Triple(colors.surfaceVariant, colors.onSurfaceVariant, "Sibuk")
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 8.451.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).background(content, CircleShape))
        Text(
            text = label,
            color = content,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        )
    }
}

@Composable
private fun InfoItem(icon: Int, text: String) {
    val colors = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), contentDescription = null, tint = colors.outline, modifier = Modifier.size(14.dp))
        Text(
            text = text,
            color = colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}

@Composable
private fun SkillChip(text: String, muted: Boolean = false, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (muted) colors.surfaceVariant.copy(alpha = 0.6f) else colors.tertiaryContainer)
            .padding(horizontal = 8.451.dp, vertical = 4.dp),
        color = if (muted) colors.outline else colors.onTertiaryContainer,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall.withFrameMetrics(
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    )
}
