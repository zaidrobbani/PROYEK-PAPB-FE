package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.ProjectSummaryChip
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/** Teks chip status Menunggu Kesepakatan di Figma (#544222); belum ada di tema, disimpan lokal. */
private val OnStatusChip = Color(0xFF544222)

/** Figma memakai line-height 1.4 untuk teks di screen ini. */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp)

@Composable
private fun StatusChip(@DrawableRes icon: Int, label: String) {
    Row(
        modifier = Modifier
            .background(ProjectSummaryChip, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(icon), null, tint = OnStatusChip, modifier = Modifier.size(15.dp))
        Text(label, color = OnStatusChip, style = text(FontWeight.SemiBold, 11.6f))
    }
}

data class NegotiationParty(val initials: String, val name: String, val caption: String, val isUmkm: Boolean)

@Composable
private fun Avatar(initials: String, isUmkm: Boolean, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(if (isUmkm) SandContainer else SageBadge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials,
            color = if (isUmkm) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
            style = text(FontWeight.Bold, 12.7f)
        )
    }
}

/** Kartu atas: chip status project & pembayaran, judul, serta UMKM ⇄ freelancer. */
@Composable
fun NegotiationHeaderCard(
    statusLabel: String,
    statusCancelled: Boolean,
    paymentLabel: String,
    projectTitle: String,
    umkm: NegotiationParty,
    freelancer: NegotiationParty
) {
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatusChip(if (statusCancelled) R.drawable.ic_symbol_cancel else R.drawable.ic_symbol_hourglass_top, statusLabel)
            StatusChip(R.drawable.ic_symbol_schedule, paymentLabel)
        }
        Text(projectTitle, color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 16.9f))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Party(umkm)
            Icon(painterResource(R.drawable.ic_symbol_sync_alt), null, tint = colors.outline, modifier = Modifier.size(19.dp))
            Party(freelancer)
        }
    }
}

@Composable
private fun Party(party: NegotiationParty) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Avatar(party.initials, party.isUmkm, 34)
        Column {
            Text(party.name, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, style = text(FontWeight.SemiBold, 12.7f))
            Text(party.caption, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, style = text(FontWeight.Normal, 11.6f))
        }
    }
}

/** Satu baris ringkasan; [emphasis] = nilai tebal (Nilai project). */
data class SummaryRow(val label: String, val value: String, val emphasis: Boolean = false, val dividerBefore: Boolean = false)

@Composable
fun AgreementSummaryCard(rows: List<SummaryRow>) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.tertiaryContainer, RoundedCornerShape(16.dp))
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_symbol_description), null, tint = colors.primary, modifier = Modifier.size(21.dp))
            Text("Ringkasan Kesepakatan", color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 15.8f))
        }
        rows.forEach { row ->
            if (row.dividerBefore) HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(row.label, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 13.7f))
                Text(
                    row.value,
                    modifier = Modifier.weight(1f),
                    color = colors.onSurface,
                    textAlign = TextAlign.End,
                    style = text(if (row.emphasis) FontWeight.Bold else FontWeight.SemiBold, 13.7f)
                )
            }
        }
    }
}

@Composable
fun NegotiationDateChip(label: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            label,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = text(FontWeight.SemiBold, 11.6f)
        )
    }
}

/** Bubble negosiasi. Keluar: cokelat bergaris di kanan. Masuk: putih dengan avatar freelancer. */
@Composable
fun NegotiationBubble(text: String, time: String, outgoing: Boolean, freelancerInitials: String) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (outgoing) Arrangement.End else Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (!outgoing) Avatar(freelancerInitials, isUmkm = false, size = 32)
        Column(
            modifier = Modifier
                .widthIn(max = 250.dp + 28.dp)
                .clip(shape)
                .background(if (outgoing) colors.primary else colors.surfaceContainerLowest)
                .then(if (outgoing) Modifier.border(1.dp, colors.outlineVariant, shape) else Modifier)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text, color = if (outgoing) colors.onPrimary else colors.onSurface, style = text(FontWeight.Normal, 13.7f))
            Text(time, color = if (outgoing) SandContainer else colors.outline, style = text(FontWeight.Medium, 10.6f))
        }
    }
}

@Composable
fun NegotiationInfoNote() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SageBadge.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(painterResource(R.drawable.ic_symbol_verified_user), null, tint = colors.secondary, modifier = Modifier.size(19.dp))
        Text(
            "Sebelum pembayaran, kedua pihak masih bisa melanjutkan atau membatalkan project tanpa biaya.",
            modifier = Modifier.weight(1f),
            color = colors.onSecondaryContainer,
            style = text(FontWeight.Normal, 12.7f)
        )
    }
}

/** Bar bawah: Batalkan (garis merah) dan Lanjut Project. Tombol nonaktif tampil redup. */
@Composable
fun NegotiationActionBar(
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    cancelEnabled: Boolean,
    continueEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth().background(colors.background.copy(alpha = 0.97f))) {
        HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
        Row(
            modifier = Modifier.padding(horizontal = 17.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(R.drawable.ic_symbol_close, "Batalkan", colors.error, Color.Transparent, colors.error, onCancel, cancelEnabled)
            ActionButton(R.drawable.ic_symbol_arrow_forward, "Lanjut Project", colors.onPrimary, colors.primary, null, onContinue, continueEnabled)
        }
    }
}

@Composable
private fun RowScope.ActionButton(
    @DrawableRes icon: Int,
    label: String,
    content: Color,
    background: Color,
    border: Color?,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .weight(1f)
            .height(51.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .background(background)
            .then(if (border != null) Modifier.border(1.5.dp, border, shape) else Modifier)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(icon), null, tint = content, modifier = Modifier.size(19.dp))
        Text(label, color = content, maxLines = 1, style = text(FontWeight.SemiBold, 14.8f))
    }
}
