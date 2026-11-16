package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.cancel_project_dialog.container.CancelProjectDialog
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.AgreementSummaryCard
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationActionBar
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationBubble
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationDateChip
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationHeaderCard
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationInfoNote
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.NegotiationParty
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.component.SummaryRow
import com.example.proyek_papb_fe.feature.umkm.data.ContinueProjectResult
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val BubbleTime = DateTimeFormatter.ofPattern("HH.mm")
private val OlderDate = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

/**
 * Negosiasi UMKM untuk percakapan tahap Negosiasi (Blueprint TK-F-U-15 Lanjut Project,
 * TK-F-U-16 Batalkan Project). Data dari [UmkmNegotiationStore] dan pesan dari
 * [UmkmConversationStore], semuanya DATA DUMMY lokal.
 *
 * Batalkan membuka Modal Batalkan Project di atas screen ini. [onCancelled] dipanggil setelah
 * pembatalan berhasil (null = screen Project Dibatalkan belum dibuat). [onBothAgreed] dipanggil bila kedua
 * pihak sudah setuju (tahap berikutnya: rencana pengerjaan & pembayaran).
 */
@Composable
fun NegotiationScreen(
    conversationId: String,
    onBack: () -> Unit,
    onCancelled: ((conversationId: String) -> Unit)?,
    onBothAgreed: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    val conversation = UmkmConversationStore.conversation(conversationId)
    val negotiation = UmkmNegotiationStore.negotiation(conversationId)
    if (conversation == null || negotiation == null) {
        // Tidak ada di Figma: percakapan tidak sedang dalam negosiasi.
        Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AuthAppBar(title = "Negosiasi", onBack = onBack)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Data negosiasi tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    // Membuka negosiasi juga membaca pesan baru di percakapan ini.
    LaunchedEffect(conversationId) { UmkmConversationStore.markRead(conversationId) }

    var showCancel by rememberSaveable { mutableStateOf(false) }
    val cancelled = negotiation.status == ProjectStatus.Dibatalkan

    val profile = UmkmAccount.profile
    val messages = UmkmConversationStore.messages(conversationId)
    val today = LocalDate.now()
    val rows = listOf(
        SummaryRow("Nilai project", negotiation.agreedValue?.let(::formatRupiah) ?: "Belum disepakati", emphasis = true),
        SummaryRow("Durasi pengerjaan", negotiation.duration),
        SummaryRow("Output", negotiation.output),
        SummaryRow("Metode bayar", negotiation.paymentMethod, dividerBefore = true)
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AuthAppBar(title = "Negosiasi", onBack = onBack)
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Figma: pt 84 (di bawah App Bar 64), pb 120 di atas bar aksi yang menempel.
                    contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 84.dp - 64.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(key = "header") {
                        NegotiationHeaderCard(
                            statusLabel = negotiation.status.label,
                            statusCancelled = cancelled,
                            paymentLabel = if (negotiation.isPaid) "Sudah dibayar" else "Belum dibayar",
                            projectTitle = conversation.projectTitle,
                            umkm = NegotiationParty(profile.initials, profile.businessName, "${profile.ownerName}, UMKM", isUmkm = true),
                            freelancer = NegotiationParty(conversation.initials, conversation.freelancerName, "Freelancer", isUmkm = false)
                        )
                    }
                    item(key = "summary") { AgreementSummaryCard(rows) }
                    messages.forEachIndexed { index, message ->
                        val day = message.sentAt.toLocalDate()
                        if (index == 0 || messages[index - 1].sentAt.toLocalDate() != day) {
                            item(key = "date-$day") {
                                NegotiationDateChip(
                                    when (day) {
                                        today -> "Hari ini"
                                        today.minusDays(1) -> "Kemarin"
                                        else -> day.format(OlderDate)
                                    }
                                )
                            }
                        }
                        item(key = message.id) {
                            NegotiationBubble(
                                text = message.text,
                                time = message.sentAt.format(BubbleTime),
                                outgoing = message.fromUmkm,
                                freelancerInitials = conversation.initials
                            )
                        }
                    }
                    item(key = "info") { NegotiationInfoNote() }
                }
                NegotiationActionBar(
                    onCancel = { showCancel = true },
                    cancelEnabled = !cancelled && !negotiation.isPaid,
                    onContinue = {
                        when (UmkmNegotiationStore.continueProject(conversationId)) {
                            ContinueProjectResult.WaitingFreelancer ->
                                toast("Persetujuanmu tercatat. Menunggu freelancer menekan Lanjut.")
                            ContinueProjectResult.BothAgreed ->
                                onBothAgreed?.invoke() ?: toast("Kedua pihak setuju. Status: Menunggu Pembayaran.")
                            ContinueProjectResult.IncompleteAgreement ->
                                toast("Nilai project belum disepakati. Sepakati dulu lewat chat sebelum melanjutkan.")
                            ContinueProjectResult.NotAllowed -> Unit
                        }
                    },
                    // Setelah UMKM setuju, Lanjut tidak bisa ditekan lagi (persetujuan sudah tercatat).
                    continueEnabled = !cancelled && !negotiation.umkmAgreed && !negotiation.isPaid,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        // Modal di atas seluruh screen Negosiasi (termasuk App Bar), seperti Figma.
        CancelProjectDialog(
            visible = showCancel,
            onDismiss = { showCancel = false },
            onConfirm = { reason ->
                showCancel = false
                if (UmkmNegotiationStore.cancelProject(conversationId, reason)) {
                    onCancelled?.invoke(conversationId) ?: toast("Project dibatalkan (simulasi lokal).")
                } else {
                    toast("Project tidak bisa dibatalkan karena pembayaran sudah berhasil.")
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NegotiationScreenPreview() {
    PROYEKPAPBFETheme {
        NegotiationScreen(conversationId = "dummy-chat-maya", onBack = {}, onCancelled = null, onBothAgreed = null)
    }
}
