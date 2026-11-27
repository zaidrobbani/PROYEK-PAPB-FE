package com.example.proyek_papb_fe.core.ui.components.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val SelectedRow = TemuKaryaColors.ActionPrimary.copy(alpha = 0.14f)

/**
 * Baris pesan yang bisa dipilih ala WhatsApp: tekan lama untuk mulai memilih, lalu ketuk untuk
 * menambah/membuang. Di luar mode pilih, ketukan biasa tidak ditangkap (lampiran tetap bisa dibuka).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.selectableMessage(
    selected: Boolean,
    selectionMode: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit
): Modifier {
    if (!enabled) return this
    val haptic = LocalHapticFeedback.current
    return this
        .semantics { this.selected = selected }
        .background(if (selected) SelectedRow else Color.Transparent)
        .combinedClickable(
            onClickLabel = if (selectionMode) "Pilih pesan" else null,
            onLongClickLabel = "Pilih pesan",
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle()
            },
            onClick = { if (selectionMode) onToggle() }
        )
}

/** Pengganti top bar saat memilih pesan: tutup, jumlah terpilih, hapus. */
@Composable
fun MessageSelectionBar(
    count: Int,
    isDeleting: Boolean,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(TemuKaryaColors.BgCanvas)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(TemuKaryaColors.Divider, Offset(0f, size.height - stroke / 2), Offset(size.width, size.height - stroke / 2), stroke)
            }
            .padding(start = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Batal memilih", tint = TemuKaryaColors.TextPrimary)
        }
        Text(
            text = "$count dipilih",
            style = TemuKaryaTypography.DetailTopBarTitle,
            color = TemuKaryaColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (isDeleting) {
            CircularProgressIndicator(
                color = TemuKaryaColors.ActionPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.padding(12.dp).size(24.dp)
            )
        } else {
            IconButton(onClick = onDelete, enabled = count > 0) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus pesan terpilih", tint = TemuKaryaColors.TextPrimary)
            }
        }
    }
}

/**
 * Konfirmasi hapus ala WhatsApp. "Hapus untuk semua orang" hanya muncul bila semua pesan terpilih
 * milik sendiri dan masih ≤ 48 jam.
 */
@Composable
fun DeleteMessagesDialog(
    count: Int,
    canDeleteForEveryone: Boolean,
    onDeleteForMe: () -> Unit,
    onDeleteForEveryone: () -> Unit,
    onDismiss: () -> Unit
) {
    val danger = MaterialTheme.colorScheme.error
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (count == 1) "Hapus pesan?" else "Hapus $count pesan?") },
        text = {
            Text(
                if (canDeleteForEveryone) "Pesan yang dihapus untuk semua orang akan diganti tulisan \"Pesan ini telah dihapus\"."
                else "Pesan hanya dihapus dari perangkatmu. Lawan bicara tetap bisa melihatnya."
            )
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (canDeleteForEveryone) {
                    TextButton(onClick = onDeleteForEveryone) { Text("Hapus untuk semua orang", color = danger) }
                }
                TextButton(onClick = onDeleteForMe) { Text("Hapus untuk saya", color = danger) }
                TextButton(onClick = onDismiss) { Text("Batal") }
            }
        }
    )
}

/** Isi bubble pesan yang sudah dihapus untuk semua orang. */
@Composable
fun DeletedMessageText(mine: Boolean, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = if (mine) "🚫 Anda menghapus pesan ini" else "🚫 Pesan ini telah dihapus",
        style = TemuKaryaTypography.BodyLarge.copy(fontStyle = FontStyle.Italic),
        color = color.copy(alpha = 0.8f),
        modifier = modifier
    )
}
