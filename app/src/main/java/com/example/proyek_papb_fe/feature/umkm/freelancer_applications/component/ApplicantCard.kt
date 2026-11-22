package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.core.ui.components.InitialsAvatar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnMatchChip
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ProjectApplication
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

enum class ApplicantSelection { Open, Selected, Closed }

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight
    )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicantCard(
    application: ProjectApplication,
    selection: ApplicantSelection,
    onViewProfile: () -> Unit,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    val top = application.isTopRecommendation

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (top) Modifier.registerInputShadow(shape)
                else Modifier.authDropShadow(shape, AuthShadowStyle.Subtle)
            )
            .background(colors.tertiaryContainer, shape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.677.dp)) {
            val photo = application.photo
            if (photo != null) {
                Image(
                    painter = painterResource(photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(59.159.dp)
                        .registerInputShadow(CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                InitialsAvatar(
                    name = application.freelancerName,
                    modifier = Modifier
                        .size(59.159.dp)
                        .registerInputShadow(CircleShape),
                    fontSize = 18.sp
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (top) 25.354.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(if (top) 2.113.dp else 0.dp)
            ) {
                Text(
                    text = application.freelancerName,
                    color = colors.onTertiaryContainer,
                    style = text(FontWeight.SemiBold, 16.903.sp, 23.241.sp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                    verticalArrangement = Arrangement.spacedBy(6.338.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    if (application.verified) VerifiedChip()
                    application.matchPercent?.let { MatchChip(it) }
                    if (!top) InlineRating(application.rating, application.reviewLabel)
                    application.badge?.let {
                        Text(
                            text = it,
                            color = colors.secondary,
                            style = text(FontWeight.Medium, 12.677.sp, 16.903.sp)
                        )
                    }
                }
                Text(
                    text = application.specialty,
                    modifier = Modifier.padding(top = 4.226.dp),
                    color = colors.onSurfaceVariant,
                    style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
                )
            }
        }
        if (top) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceContainerLow, RoundedCornerShape(8.451.dp))
                    .padding(8.451.dp),
                horizontalArrangement = Arrangement.spacedBy(12.677.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_project_list_star), contentDescription = null)
                    Text(
                        text = application.rating,
                        color = colors.onTertiaryContainer,
                        style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                    )
                    Text(
                        text = application.reviewLabel,
                        color = colors.onSurfaceVariant,
                        style = text(FontWeight.Normal, 11.621.sp, 14.79.sp)
                    )
                }
                Image(painter = painterResource(R.drawable.ic_applications_history), contentDescription = null)
            }
        }
        BidStats(application)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (top) 4.226.dp else 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.451.dp)
        ) {
            ActionButton(
                text = "Lihat Profil",
                iconRes = R.drawable.ic_applications_profile,
                background = colors.surfaceContainerLowest,
                contentColor = colors.onTertiaryContainer,
                enabled = true,
                onClick = onViewProfile
            )
            ActionButton(
                text = if (selection == ApplicantSelection.Selected) "Freelancer Terpilih" else "Setujui & Buat",
                iconRes = R.drawable.ic_applications_approve,
                background = colors.primary,
                contentColor = colors.onPrimary,
                enabled = selection == ApplicantSelection.Open,
                dimmed = selection == ApplicantSelection.Closed,
                onClick = onApprove
            )
        }
    }
}

@Composable
private fun VerifiedChip() {
    Row(
        modifier = Modifier
            .background(SageBadge, CircleShape)
            .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
        horizontalArrangement = Arrangement.spacedBy(2.113.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_applications_verified), contentDescription = null)
        Text(text = "Terverifikasi", color = OnSageBadge, style = text(FontWeight.Normal, 11.621.sp, 21.128.sp))
    }
}

@Composable
private fun MatchChip(percent: Int) {
    Text(
        text = "Cocok $percent%",
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
        color = OnMatchChip,
        style = text(FontWeight.Bold, 11.621.sp, 21.128.sp)
    )
}

@Composable
private fun InlineRating(rating: String, reviewLabel: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_applications_star), contentDescription = null)
        Text(
            text = rating,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
        )
        Text(
            text = reviewLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = text(FontWeight.Normal, 11.621.sp, 14.79.sp)
        )
    }
}

@Composable
private fun BidStats(application: ProjectApplication) {
    val colors = MaterialTheme.colorScheme
    val top = application.isTopRecommendation
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(colors.surfaceContainerLowest, shape)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (top) "Penawaran Biaya" else "Tawaran Harga",
                color = colors.onSurfaceVariant,
                style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
            )
            Text(
                text = formatRupiah(application.bid),
                color = colors.onTertiaryContainer,
                style = text(FontWeight.Bold, 16.903.sp, 23.241.sp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (application.note != null) 21.128.dp else 0.dp)
        ) {
            Text(
                text = if (top) "Waktu Kerja" else "Durasi Pengerjaan",
                color = colors.onSurfaceVariant,
                style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        if (top) R.drawable.ic_applications_bolt else R.drawable.ic_applications_duration
                    ),
                    contentDescription = null
                )
                Text(
                    text = "${application.workingDays} Hari Kerja",
                    color = colors.onSurface,
                    style = text(FontWeight.SemiBold, 16.903.sp, 23.241.sp)
                )
            }
            application.note?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 2.641.dp),
                    color = colors.onSurfaceVariant,
                    style = text(FontWeight.Normal, 11.621.sp, 21.128.sp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.ActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    background: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    dimmed: Boolean = false
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .weight(1f)
            .defaultMinSize(minHeight = 50.708.dp)
            .alpha(if (dimmed) 0.4f else 1f)
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.677.dp, vertical = 15.846.dp),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
        Text(
            text = text,
            color = contentColor,
            maxLines = 1,
            style = text(FontWeight.SemiBold, 14.79.sp, 19.015.sp)
        )
    }
}
