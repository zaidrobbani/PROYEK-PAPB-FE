package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import kotlinx.coroutines.launch

private val SheetButtonHeight = 51.dp
private val OptionShape = RoundedCornerShape(12.dp)

/**
 * Figma: "Bottom Sheet Batalkan" (26:172) di atas frame Negosiasi (26:87).
 * Stateless: alasan terpilih dan aksi dikelola container. Sheet ditutup dengan
 * animasi dulu, baru callback dipanggil, agar tidak hilang mendadak.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelProjectBottomSheet(
    selectedReason: CancellationReason,
    onReasonSelected: (CancellationReason) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // Cegah tap ganda selama animasi tutup (mis. konfirmasi terpanggil dua kali).
    var isClosing by remember { mutableStateOf(false) }
    val hideThen: (() -> Unit) -> Unit = { action ->
        if (!isClosing) {
            isClosing = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = NegotiationSheetShape,
        containerColor = TemuKaryaColors.BgCanvas,
        contentColor = TemuKaryaColors.TextPrimary,
        tonalElevation = 0.dp,
        scrimColor = NegotiationColors.SheetScrim,
        dragHandle = {
            // Figma: pt 12, handle 40×4 radius 2, lalu gap 14 ke konten.
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 14.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(NegotiationColors.SheetHandle, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Konten tidak di-scroll di Figma; scroll hanya aktif di layar pendek.
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SheetHeader()
            Text(
                text = "Belum ada dana yang dibayarkan, jadi pembatalan tidak dikenai biaya. " +
                    "Pihak lain akan menerima notifikasi.",
                style = NegotiationTypography.SheetBody,
                color = TemuKaryaColors.TextBody,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Alasan pembatalan",
                style = NegotiationTypography.FieldLabel,
                color = TemuKaryaColors.TextBody
            )
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CancellationReason.entries.forEach { reason ->
                    ReasonOption(
                        label = reason.label,
                        selected = reason == selectedReason,
                        onClick = { onReasonSelected(reason) }
                    )
                }
            }
            SheetButton(
                text = "Ya, Batalkan Proyek",
                filled = true,
                onClick = { hideThen(onConfirm) }
            )
            SheetButton(
                text = "Kembali ke Negosiasi",
                filled = false,
                onClick = { hideThen(onDismiss) }
            )
        }
    }
}

@Composable
private fun SheetHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(NegotiationColors.DangerContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_warning),
                contentDescription = null,
                tint = NegotiationColors.Danger,
                modifier = Modifier.size(21.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Batalkan proyek ini?",
                style = NegotiationTypography.SheetTitle,
                color = TemuKaryaColors.TextHeading
            )
            Text(
                text = "Status akan berubah menjadi Dibatalkan",
                style = NegotiationTypography.SheetSubtitle,
                color = TemuKaryaColors.TextBody
            )
        }
    }
}

/** Figma: "Opsi …" (26:183–26:192) — terpilih: krem + border cokelat; lainnya putih. */
@Composable
private fun ReasonOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OptionShape)
            .background(if (selected) TemuKaryaColors.SurfaceContainer else TemuKaryaColors.SurfaceWhite)
            .border(
                width = 1.dp,
                color = if (selected) TemuKaryaColors.ActionPrimary else NegotiationColors.Outline,
                shape = OptionShape
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                if (selected) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked
            ),
            contentDescription = null,
            tint = if (selected) TemuKaryaColors.ActionPrimary else TemuKaryaColors.TextPlaceholder,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = label,
            style = if (selected) NegotiationTypography.OptionSelected else NegotiationTypography.Option,
            color = TemuKaryaColors.TextPrimary
        )
    }
}

@Composable
private fun SheetButton(text: String, filled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SheetButtonHeight)
            .clip(NegotiationButtonShape)
            .then(
                if (filled) {
                    Modifier.background(NegotiationColors.Danger)
                } else {
                    Modifier.border(1.5.dp, TemuKaryaColors.ActionPrimary, NegotiationButtonShape)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = NegotiationTypography.Button,
            color = if (filled) TemuKaryaColors.OnAction else TemuKaryaColors.ActionPrimary,
            maxLines = 1
        )
    }
}
