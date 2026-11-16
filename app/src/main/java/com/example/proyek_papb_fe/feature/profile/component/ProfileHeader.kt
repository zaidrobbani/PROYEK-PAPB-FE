package com.example.proyek_papb_fe.feature.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/**
 * Figma: kartu identitas "Profil Saya (Freelancer)" (27:262): avatar inisial 64, nama,
 * keahlian utama, kota, lalu chip rating dan ketersediaan.
 */
@Composable
internal fun ProfileHeader(
    initials: String,
    photoUri: String?,
    name: String,
    headline: String,
    city: String,
    ratingLabel: String,
    isAvailableForWork: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, ProfileCardShape)
            .border(1.dp, ProfileColors.CardBorder, ProfileCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalImage(
                uri = photoUri,
                fallbackRes = null,
                contentDescription = "Foto profil",
                modifier = Modifier.size(64.dp).clip(CircleShape),
                placeholder = {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(ProfileColors.Avatar, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, style = ProfileTypography.Initials, color = ProfileColors.OnAvatar)
                    }
                }
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    style = ProfileTypography.Name,
                    color = TemuKaryaColors.TextHeading,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = headline,
                    style = ProfileTypography.Headline,
                    color = TemuKaryaColors.TextBody,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile_location),
                        contentDescription = null,
                        tint = TemuKaryaColors.TextPlaceholder
                    )
                    Text(city, style = ProfileTypography.Caption, color = TemuKaryaColors.TextPlaceholder)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileChip(
                text = ratingLabel,
                background = ProfileColors.RatingChip,
                content = TemuKaryaColors.ActionPrimary,
                iconRes = R.drawable.ic_profile_star
            )
            if (isAvailableForWork) {
                ProfileChip(
                    text = "Siap Terima Proyek",
                    background = ProfileColors.AvailableChip,
                    content = ProfileColors.OnAvailableChip
                )
            }
        }
    }
}

/** Pill chip Figma (px 12, py 4, radius penuh, ikon 15 opsional). */
@Composable
internal fun ProfileChip(
    text: String,
    background: Color,
    content: Color,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {
    Row(
        modifier = modifier
            .background(background, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let { Icon(painterResource(it), contentDescription = null, tint = content) }
        Text(text, style = ProfileTypography.Chip, color = content, maxLines = 1)
    }
}

@Preview(showBackground = true, widthDp = 412, backgroundColor = 0xFFFEF7E6)
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        initials = "AR",
        photoUri = null,
        name = "Andi Ramadhan",
        headline = "Desainer Grafis & Identitas Kemasan",
        city = "Yogyakarta",
        ratingLabel = "4.9, 48 ulasan",
        isAvailableForWork = true,
        modifier = Modifier.padding(17.dp)
    )
}
