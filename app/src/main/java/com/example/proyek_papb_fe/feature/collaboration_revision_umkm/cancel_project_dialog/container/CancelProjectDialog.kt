package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.cancel_project_dialog.container

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.cancel_project_dialog.component.CancelProjectSheet
import com.example.proyek_papb_fe.feature.umkm.data.UmkmCancellationReason

/**
 * Modal Batalkan Project (UMKM) di atas screen Negosiasi. Bukan route sendiri dan bukan
 * ModalBottomSheet Material, agar scrim dan bentuk sheet sama dengan Figma.
 *
 * Pilihan awal "Harga belum sesuai" mengikuti Figma (dan sheet versi freelancer); konfirmasi
 * tetap diperiksa wajib memilih alasan. Scrim, Back, dan "Kembali ke Negosiasi" menutup modal
 * tanpa mengubah apa pun. [onConfirm] menerima alasan terpilih; perubahan status dilakukan
 * pemanggil sesuai aturan Blueprint.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CancelProjectDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (UmkmCancellationReason) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = visible, onBack = onDismiss)
    AnimatedVisibility(visible = visible, modifier = modifier, enter = fadeIn(), exit = fadeOut()) {
        // Dibuat ulang setiap modal dibuka, jadi pilihan kembali ke bawaan Figma.
        var selectedName by rememberSaveable { mutableStateOf<String?>(UmkmCancellationReason.entries.first().name) }
        val selected = selectedName?.let { UmkmCancellationReason.valueOf(it) }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Tutup",
                        onClick = onDismiss
                    )
            )
            CancelProjectSheet(
                selected = selected,
                onSelect = { selectedName = it.name },
                canConfirm = selected != null,
                onConfirm = { selected?.let(onConfirm) },
                onBackToNegotiation = onDismiss,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .animateEnterExit(enter = slideInVertically { it }, exit = slideOutVertically { it })
                    // Sentuhan di dalam sheet tidak diteruskan ke scrim.
                    .pointerInput(Unit) { detectTapGestures { } }
            )
        }
    }
}
