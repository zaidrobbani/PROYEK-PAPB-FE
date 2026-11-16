package com.example.proyek_papb_fe.feature.umkm.profile.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.umkm.component.shared.UriImage
import com.example.proyek_papb_fe.core.ui.theme.OnVerifiedChip
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.core.ui.theme.VerifiedChip
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProfile

/** Figma memakai line-height 1.4 untuk teks di screen ini. */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

/** Kartu identitas: avatar inisial, nama, usaha, kota, chip verifikasi dan kategori. */
@Composable
fun ProfileHeaderCard(profile: UmkmProfile, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .border(1.dp, colors.outlineVariant, shape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initialsAvatar: @Composable () -> Unit = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(SandContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = profile.initials, color = colors.primary, style = text(FontWeight.Bold, 19f))
                }
            }
            // Foto dari Kelola Profil Usaha; tanpa foto tetap inisial seperti Figma.
            val photo = profile.photoUri
            if (photo != null) {
                UriImage(
                    uri = photo,
                    contentDescription = "Foto profil",
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    fallback = initialsAvatar
                )
            } else {
                initialsAvatar()
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = profile.ownerName, color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 19f))
                Text(
                    text = "Pemilik ${profile.businessName}",
                    color = colors.onSurfaceVariant,
                    style = text(FontWeight.Normal, 12.7f)
                )
                Text(text = profile.city, color = colors.outline, style = text(FontWeight.Normal, 11.6f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (profile.isVerified) {
                Row(
                    modifier = Modifier
                        .background(VerifiedChip, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_symbol_verified),
                        contentDescription = null,
                        tint = OnVerifiedChip,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(text = "UMKM Terverifikasi", color = OnVerifiedChip, style = text(FontWeight.SemiBold, 11.6f))
                }
            }
            Text(
                text = profile.businessCategory,
                modifier = Modifier
                    .background(colors.surfaceVariant, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = colors.primary,
                style = text(FontWeight.SemiBold, 11.6f)
            )
        }
    }
}

data class ProfileStat(val value: String, val label: String)

/** Tiga kotak ringkasan: Project dibuat, Selesai, Total transaksi. */
@Composable
fun ProfileStatsRow(stats: List<ProfileStat>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(colors.tertiaryContainer, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stat.value, color = colors.onTertiaryContainer, maxLines = 1, style = text(FontWeight.Bold, 18f))
                Text(text = stat.label, color = colors.onSurfaceVariant, maxLines = 1, style = text(FontWeight.Normal, 11.6f))
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        style = text(FontWeight.Bold, 15.8f)
    )
}

/** Baris menu Kelola Akun. [destructive] = gaya merah untuk "Keluar". */
@Composable
fun ProfileMenuItem(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val accent: Color = if (destructive) colors.error else colors.primary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(if (destructive) colors.errorContainer else colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(icon), contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = if (destructive) colors.error else colors.onSurface,
                style = text(FontWeight.SemiBold, 14.8f)
            )
            Text(text = subtitle, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 11.6f))
        }
        Icon(
            painter = painterResource(R.drawable.ic_symbol_chevron_right),
            contentDescription = null,
            tint = colors.outline,
            modifier = Modifier.size(21.dp)
        )
    }
}
