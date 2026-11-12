package com.example.proyek_papb_fe.feature.profile.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Satu kotak statistik (Figma 27:279): nilai tebal + label. */
data class ProfileStat(val value: String, val label: String)

/** Figma 27:278: tiga kotak statistik lebar sama, jarak 10. */
@Composable
internal fun ProfileStatsRow(stats: List<ProfileStat>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(TemuKaryaColors.SurfaceContainer, ProfileCardShape)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(stat.value, style = ProfileTypography.StatValue, color = TemuKaryaColors.TextHeading, maxLines = 1)
                Text(stat.label, style = ProfileTypography.Caption, color = TemuKaryaColors.TextBody, maxLines = 1)
            }
        }
    }
}

/** Judul bagian ("Keahlian", "Kelola Akun") dengan aksi teks opsional di kanan ("Kelola"). */
@Composable
internal fun ProfileSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = ProfileTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = ProfileTypography.Link,
                color = TemuKaryaColors.ActionPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(role = Role.Button, onClick = onActionClick)
            )
        }
    }
}

/** Figma 27:291: chip keahlian hijau, membungkus ke baris berikutnya (jarak 8). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProfileSkills(skills: List<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skills.forEach { skill ->
            ProfileChip(text = skill, background = ProfileColors.SkillChip, content = ProfileColors.OnSkillChip)
        }
    }
}

/**
 * Figma: baris menu "Kelola Akun" (27:302): lencana ikon 38, judul + keterangan, chevron.
 * [isDanger] = baris "Keluar" (lencana & judul merah).
 */
@Composable
internal fun ProfileMenuItem(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    val badgeColor: Color = if (isDanger) ProfileColors.DangerBackground else ProfileColors.MenuIconBackground
    val iconColor: Color = if (isDanger) ProfileColors.Danger else TemuKaryaColors.ActionPrimary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ProfileCardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), contentDescription = null, tint = iconColor)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = ProfileTypography.MenuTitle,
                color = if (isDanger) ProfileColors.Danger else TemuKaryaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = ProfileTypography.Caption,
                color = TemuKaryaColors.TextBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_profile_chevron_right),
            contentDescription = null,
            tint = TemuKaryaColors.TextPlaceholder
        )
    }
}
