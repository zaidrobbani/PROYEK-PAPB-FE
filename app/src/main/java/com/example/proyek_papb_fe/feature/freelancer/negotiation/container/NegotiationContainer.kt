package com.example.proyek_papb_fe.feature.freelancer.negotiation.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.CurrentFreelancer
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.NegotiationDummyData
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.CancelProjectBottomSheet
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationParty
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationScreen
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationUiState

/** 1500000 → "Rp 1.500.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** "Rina Pratiwi" → "RP". */
private fun initialsOf(name: String): String =
    name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

/** Alasan terpilih saat modal dibuka — sesuai Figma 26:87 ("Harga belum sesuai"). */
private val DefaultCancellationReason = CancellationReason.PRICE_MISMATCH

/**
 * Container "Negosiasi": lookup project + penawaran terkirim, lalu menyusun
 * ringkasan kesepakatan dan percakapan milik project ini saja.
 * Juga memegang state modal "Batalkan Project" (26:87) dan mencatat pembatalan
 * ke [SubmittedOfferStore] untuk projectId ini saja.
 */
@Composable
fun NegotiationContainer(
    projectId: Int,
    onBack: () -> Unit,
    /** Dipanggil setelah pembatalan tersimpan dan modal tertutup. */
    onProjectCancelled: (projectId: Int) -> Unit,
    onContinueProject: (projectId: Int) -> Unit
) {
    val project = remember(projectId) { FreelancerProjectDummyData.getById(projectId) }
    // Dibaca langsung (snapshot state) agar ringkasan selalu sesuai isi store.
    val offer = SubmittedOfferStore.get(projectId)
    // Diperiksa sekali saat layar dibuka: project yang sudah dibatalkan tidak boleh
    // dinegosiasikan lagi. Tidak reaktif, agar layar tidak berkedip ke placeholder
    // selama animasi keluar setelah pengguna sendiri membatalkan.
    val wasCancelledOnEntry = remember(projectId) { offer?.isCancelled == true }

    var isCancelSheetVisible by rememberSaveable(projectId) { mutableStateOf(false) }
    var selectedReason by rememberSaveable(projectId) { mutableStateOf(DefaultCancellationReason) }

    if (wasCancelledOnEntry) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Negosiasi", onBack = onBack)
            FeaturePlaceholder(
                title = "Project dibatalkan",
                description = "Negosiasi untuk project ini sudah dibatalkan dan tidak dapat dilanjutkan."
            )
        }
        return
    }

    if (project == null || offer == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Negosiasi", onBack = onBack)
            FeaturePlaceholder(
                title = "Negosiasi tidak ditemukan",
                description = "Belum ada penawaran terkirim untuk project dengan id $projectId."
            )
        }
        return
    }

    val state = remember(project, offer) {
        val offerAmount = formatRupiah(offer.offerAmount)
        NegotiationUiState(
            projectTitle = project.title,
            client = NegotiationParty(
                initials = initialsOf(project.ownerName),
                name = project.clientName,
                role = "${project.ownerName}, UMKM"
            ),
            freelancer = NegotiationParty(
                initials = CurrentFreelancer.INITIALS,
                name = CurrentFreelancer.NAME,
                role = "Freelancer"
            ),
            projectValue = offerAmount,
            duration = "${offer.durationDays} hari kalender",
            output = NegotiationDummyData.agreedOutputFor(projectId),
            paymentMethod = "QRIS (simulasi)",
            dayLabel = "Hari ini",
            messages = NegotiationDummyData.conversationFor(project, offerAmount, offer.durationDays)
        )
    }

    val context = LocalContext.current
    NegotiationScreen(
        state = state,
        onBack = onBack,
        onCancelClick = {
            // Pembatalan hanya sebelum kesepakatan final (tab Project "Project Berjalan").
            if (SubmittedOfferStore.get(projectId)?.isAgreed == true) {
                Toast.makeText(context, "Project sudah disepakati dan resmi dikerjakan, tidak dapat dibatalkan.", Toast.LENGTH_SHORT).show()
            } else {
                isCancelSheetVisible = true
            }
        },
        onContinueClick = { onContinueProject(projectId) }
    )

    if (isCancelSheetVisible) {
        CancelProjectBottomSheet(
            selectedReason = selectedReason,
            onReasonSelected = { selectedReason = it },
            onConfirm = {
                SubmittedOfferStore.markCancelled(projectId, selectedReason)
                isCancelSheetVisible = false
                onProjectCancelled(projectId)
            },
            onDismiss = {
                // Tutup tanpa perubahan; pilihan alasan kembali ke awal untuk pembukaan berikutnya.
                isCancelSheetVisible = false
                selectedReason = DefaultCancellationReason
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NegotiationContainerPreview() {
    SubmittedOfferStore.submit(
        SubmittedOffer(
            projectId = 101,
            offerAmount = 1_500_000,
            platformFee = 75_000,
            netAmount = 1_425_000,
            durationDays = 14,
            message = "Contoh pesan proposal.",
            submittedAtMillis = System.currentTimeMillis()
        )
    )
    NegotiationContainer(projectId = 101, onBack = {}, onProjectCancelled = {}, onContinueProject = {})
}
