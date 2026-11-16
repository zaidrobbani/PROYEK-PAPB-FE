package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.ReviewChip
import com.example.proyek_papb_fe.core.ui.theme.TextMuted
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/** Figma memakai line-height 1.4 untuk teks di screen ini. */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp)

/** Ikon silang besar, judul, chip "Dibatalkan", dan keterangan. */
@Composable
fun CancelledHero(statusLabel: String, description: String) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .background(ReviewChip, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_symbol_cancel), null, tint = TextMuted, modifier = Modifier.size(40.dp))
        }
        Text(
            "Project dibatalkan",
            modifier = Modifier.fillMaxWidth(),
            color = colors.onTertiaryContainer,
            textAlign = TextAlign.Center,
            style = text(FontWeight.Bold, 23.2f)
        )
        Text(
            statusLabel,
            modifier = Modifier
                .background(TextMuted, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = Color.White,
            style = text(FontWeight.SemiBold, 11.6f)
        )
        Text(
            description,
            modifier = Modifier.fillMaxWidth(),
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = text(FontWeight.Normal, 14.8f)
        )
    }
}

/** Satu baris rincian; [positive] = nilai hijau (baris Dana di Figma). */
data class CancelledDetail(val label: String, val value: String, val positive: Boolean = false)

@Composable
fun CancelledDetailCard(details: List<CancelledDetail>) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .border(1.dp, colors.outlineVariant, shape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        details.forEach { detail ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(detail.label, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 13.7f))
                Text(
                    detail.value,
                    modifier = Modifier.weight(1f),
                    color = if (detail.positive) colors.secondary else colors.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = text(FontWeight.SemiBold, 13.7f)
                )
            }
        }
    }
}

/** Tombol lebar penuh; [filled] = cokelat, selain itu bergaris. */
@Composable
fun CancelledActionButton(label: String, filled: Boolean, @DrawableRes icon: Int?, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val content = if (filled) colors.onPrimary else colors.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(51.dp)
            .clip(shape)
            .then(if (filled) Modifier.background(colors.primary) else Modifier.border(1.5.dp, colors.primary, shape))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) Icon(painterResource(icon), null, tint = content, modifier = Modifier.size(19.dp))
        Text(label, color = content, style = text(FontWeight.SemiBold, 14.8f))
    }
}
