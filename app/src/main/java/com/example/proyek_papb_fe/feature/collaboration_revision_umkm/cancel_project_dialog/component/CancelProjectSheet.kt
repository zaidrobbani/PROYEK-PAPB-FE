package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.cancel_project_dialog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.UmkmCancellationReason

/** Figma memakai line-height 1.4 untuk teks di modal ini. */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp)

/**
 * Isi bottom sheet "Batalkan project ini?" (Figma 270:599). Hanya tampilan; state pilihan dan
 * aksinya dikelola container. [canConfirm] false = tombol konfirmasi redup dan tidak aktif.
 */
@Composable
fun CancelProjectSheet(
    selected: UmkmCancellationReason?,
    onSelect: (UmkmCancellationReason) -> Unit,
    canConfirm: Boolean,
    onConfirm: () -> Unit,
    onBackToNegotiation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(colors.outline.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colors.errorContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_symbol_warning), null, tint = colors.error, modifier = Modifier.size(21.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Batalkan project ini?", color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 18f))
                Text("Status akan berubah menjadi Dibatalkan", color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 12.7f))
            }
        }
        Text(
            "Belum ada dana yang dibayarkan, jadi pembatalan tidak dikenai biaya. Pihak lain akan menerima notifikasi.",
            color = colors.onSurfaceVariant,
            style = text(FontWeight.Normal, 13.7f)
        )
        Text("Alasan pembatalan", color = colors.onSurfaceVariant, style = text(FontWeight.SemiBold, 12.7f))
        Column(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            UmkmCancellationReason.entries.forEach { reason ->
                ReasonOption(reason.label, reason == selected) { onSelect(reason) }
            }
        }
        SheetButton(
            label = "Ya, Batalkan Project",
            filled = true,
            enabled = canConfirm,
            onClick = onConfirm
        )
        SheetButton(label = "Kembali ke Negosiasi", filled = false, enabled = true, onClick = onBackToNegotiation)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ReasonOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) colors.tertiaryContainer else colors.surfaceContainerLowest)
            .border(1.dp, if (isSelected) colors.primary else colors.outlineVariant, shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(if (isSelected) R.drawable.ic_symbol_radio_button_checked else R.drawable.ic_symbol_radio_button_unchecked),
            contentDescription = null,
            tint = if (isSelected) colors.primary else colors.outline,
            modifier = Modifier.size(21.dp)
        )
        Text(
            label,
            color = colors.onSurface,
            style = text(if (isSelected) FontWeight.SemiBold else FontWeight.Normal, 13.7f)
        )
    }
}

@Composable
private fun SheetButton(label: String, filled: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(51.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .then(
                if (filled) Modifier.background(colors.error)
                else Modifier.border(1.5.dp, colors.primary, shape)
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (filled) colors.onError else colors.primary, style = text(FontWeight.SemiBold, 14.8f))
    }
}
