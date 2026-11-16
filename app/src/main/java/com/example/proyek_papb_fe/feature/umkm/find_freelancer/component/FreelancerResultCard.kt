package com.example.proyek_papb_fe.feature.umkm.find_freelancer.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.OnlineDotRing
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.DirectoryFreelancer
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight
    )

/** Cincin di luar lingkaran (box-shadow spread di Figma). */
private fun Modifier.outerRing(color: Color, width: Float) = drawBehind {
    drawCircle(color = color, radius = size.minDimension / 2 + width.dp.toPx())
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreelancerResultCard(
    freelancer: DirectoryFreelancer,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape,
                Shadow(
                    radius = 16.903.dp,
                    color = colors.primary.copy(alpha = 0.08f),
                    spread = (-2.113).dp,
                    offset = DpOffset(0.dp, 4.226.dp)
                )
            )
            .background(colors.tertiaryContainer, shape)
            .padding(16.903.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.677.dp)) {
            Box(
                modifier = Modifier
                    .size(59.159.dp)
                    .outerRing(SageBadge, 2.113f)
                    .clip(CircleShape)
                    .background(colors.surfaceContainerHigh)
            ) {
                Image(
                    painter = painterResource(freelancer.photo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Titik status di pojok, terpotong lingkaran foto seperti di Figma.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.79.dp)
                        .outerRing(OnlineDotRing, 2.113f)
                        .background(colors.secondary, CircleShape)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = freelancer.name,
                        color = colors.onSurface,
                        style = text(FontWeight.SemiBold, 16.903.sp, 23.241.sp)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_find_verified),
                        contentDescription = "Terverifikasi"
                    )
                }
                Text(
                    text = freelancer.role,
                    color = colors.onTertiaryContainer,
                    style = text(FontWeight.Medium, 12.677.sp, 16.903.sp)
                )
                Row(
                    modifier = Modifier.padding(top = 2.113.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_find_location), contentDescription = null)
                    Text(
                        text = freelancer.city,
                        color = colors.onSurfaceVariant,
                        style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                    )
                    Text(
                        text = "Aktif ${freelancer.lastActiveMinutes} mnt lalu",
                        modifier = Modifier.padding(start = 4.226.dp),
                        color = colors.secondary,
                        style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 12.677.dp)
                .fillMaxWidth()
                .background(colors.surfaceContainerLow, RoundedCornerShape(8.451.dp))
                .padding(8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.drawable.ic_find_rating), contentDescription = null)
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = freelancer.rating.toString(),
                            color = colors.onSurface,
                            style = text(FontWeight.SemiBold, 12.677.sp, 15.846.sp)
                        )
                        Text(
                            text = "(${freelancer.reviewCount} ulasan)",
                            color = colors.onSurfaceVariant,
                            style = text(FontWeight.Normal, 10.564.sp, 13.205.sp)
                        )
                    }
                    Text(
                        text = freelancer.ratingNote,
                        color = colors.secondary,
                        style = text(FontWeight.Normal, 10.564.sp, 21.128.sp)
                    )
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        if (freelancer.highlightIsExperience) R.drawable.ic_find_experience
                        else R.drawable.ic_find_badge
                    ),
                    contentDescription = null
                )
                Column {
                    Text(
                        text = freelancer.highlightTitle,
                        color = colors.onSurface,
                        style = text(FontWeight.SemiBold, 12.677.sp, 15.846.sp)
                    )
                    Text(
                        text = freelancer.highlightSubtitle,
                        color = colors.onSurfaceVariant,
                        style = text(FontWeight.Normal, 10.564.sp, 21.128.sp)
                    )
                }
            }
        }
        FlowRow(
            modifier = Modifier.padding(top = 12.677.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalArrangement = Arrangement.spacedBy(6.338.dp)
        ) {
            freelancer.skills.forEach { skill ->
                SkillTag(text = skill, background = SageBadge.copy(alpha = 0.6f), contentColor = OnSageBadge)
            }
            if (freelancer.moreSkillCount > 0) {
                SkillTag(
                    text = "+${freelancer.moreSkillCount} Lainnya",
                    background = colors.surfaceContainerHigh,
                    contentColor = colors.onSurfaceVariant
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.677.dp + 12.677.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mulai dari",
                    color = colors.onSurfaceVariant,
                    style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                )
                Text(
                    text = formatRupiah(freelancer.startingPrice),
                    color = colors.onTertiaryContainer,
                    style = text(FontWeight.Bold, 16.903.sp, 23.241.sp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp)) {
                val buttonShape = RoundedCornerShape(8.451.dp)
                Box(
                    modifier = Modifier
                        .height(42.256.dp)
                        .authDropShadow(buttonShape, AuthShadowStyle.Subtle)
                        .clip(buttonShape)
                        .background(colors.surfaceContainerLowest)
                        .clickable(role = Role.Button, onClick = onViewProfile)
                        .padding(horizontal = 14.79.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lihat Profil",
                        color = colors.onTertiaryContainer,
                        style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                    )
                }
                Row(
                    modifier = Modifier
                        .height(42.256.dp)
                        .authDropShadow(buttonShape, AuthShadowStyle.Subtle)
                        .clip(buttonShape)
                        .background(colors.primary)
                        .clickable(role = Role.Button, onClick = onViewProfile)
                        .padding(horizontal = 16.903.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_find_handshake), contentDescription = null)
                    // Figma memberi label yang sama dengan tombol putih; dipertahankan apa adanya.
                    Text(
                        text = "Lihat Profil",
                        color = colors.onPrimary,
                        style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillTag(text: String, background: Color, contentColor: Color) {
    Text(
        text = text,
        modifier = Modifier
            .background(background, RoundedCornerShape(6.338.dp))
            .padding(horizontal = 10.564.dp, vertical = 4.226.dp),
        color = contentColor,
        style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
    )
}
