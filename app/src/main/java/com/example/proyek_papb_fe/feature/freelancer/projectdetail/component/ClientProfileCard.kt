package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.AvatarTone
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography
import java.util.Locale

private val AvatarSize = 59.159.dp

/**
 * Figma: UMKM Client Profile Card (17:4227).
 * Pill bertitik olive di bawah lokasi (17:4253) sengaja tidak ditampilkan karena maknanya belum jelas.
 */
@Composable
internal fun ClientProfileCard(
    project: FreelancerProject,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .detailSubtleShadow(DetailCardShape)
            .clip(DetailCardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(12.677.dp),
        verticalAlignment = Alignment.Top
    ) {
        ClientAvatar(
            initials = project.clientInitials,
            tone = project.avatarTone,
            isVerified = project.isClientVerified
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.clientName,
                    style = TemuKaryaTypography.SectionTitle,
                    color = TemuKaryaColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (project.isClientVerified) {
                    Image(
                        painter = painterResource(R.drawable.ic_verified),
                        contentDescription = "Klien terverifikasi"
                    )
                }
            }

            Text(
                text = "${project.ownerName}, ${project.ownerRole}",
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextBody
            )

            Row(
                modifier = Modifier.padding(top = 8.082.dp),
                horizontalArrangement = Arrangement.spacedBy(16.67.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_location_detail),
                        contentDescription = "Lokasi"
                    )
                    Text(
                        text = project.clientLocationFull,
                        style = TemuKaryaTypography.CaptionMedium,
                        color = TemuKaryaColors.TextBody,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = "Rating"
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f", project.clientRating),
                        style = TemuKaryaTypography.CaptionSemiBold,
                        color = TemuKaryaColors.TextHeading
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientAvatar(
    initials: String,
    tone: AvatarTone,
    isVerified: Boolean
) {
    val (background, content) = when (tone) {
        AvatarTone.GREEN -> TemuKaryaColors.ChipGreen to TemuKaryaColors.OnChipGreen
        AvatarTone.SAND -> TemuKaryaColors.SurfaceSand to TemuKaryaColors.TextHeading
    }
    Box(modifier = Modifier.size(AvatarSize)) {
        Box(
            modifier = Modifier
                .size(AvatarSize)
                .detailSubtleShadow(CircleShape)
                .clip(CircleShape)
                .background(background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = TemuKaryaTypography.AvatarLarge,
                color = content,
                maxLines = 1
            )
        }
        if (isVerified) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.903.dp)
                    .clip(CircleShape)
                    .background(TemuKaryaColors.Olive),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_check_small),
                    contentDescription = null
                )
            }
        }
    }
}
