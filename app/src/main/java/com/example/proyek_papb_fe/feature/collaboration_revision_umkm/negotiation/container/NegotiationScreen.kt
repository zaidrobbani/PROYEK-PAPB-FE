package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.container

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
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
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel.UmkmChatViewModel

private val BubbleTime = DateTimeFormatter.ofPattern("HH.mm")
private val OlderDate = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

@Composable
fun NegotiationScreen(
    conversationId: String,
    onBack: () -> Unit,
    onCancelled: ((conversationId: String) -> Unit)?,
    onBothAgreed: ((projectId: String) -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: UmkmChatViewModel = viewModel(factory = UmkmChatViewModel.factory(conversationId))
) {
    val context = LocalContext.current
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    val conversation = UmkmConversationStore.conversation(conversationId)
    val negotiation = UmkmNegotiationStore.negotiation(conversationId)
    if (conversation == null || negotiation == null) {
        Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AuthAppBar(title = "Negosiasi", onBack = onBack)
            run {
                val vmState by viewModel.uiState.collectAsStateWithLifecycle()
                when {
                    vmState.isLoading -> com.example.proyek_papb_fe.core.ui.components.LoadingContent()
                    vmState.errorMessage != null -> com.example.proyek_papb_fe.core.ui.components.ErrorContent(vmState.errorMessage.orEmpty(), onRetry = viewModel::load)
                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Data negosiasi tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        return
    }

    val chatState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(chatState.message) {
        chatState.message?.let {
            toast(it)
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(chatState.readyToPayProjectId) {
        chatState.readyToPayProjectId?.let { projectId ->
            viewModel.onReadyToPayHandled()
            onBothAgreed?.invoke(projectId) ?: toast("Kedua pihak setuju. Status: Menunggu Pembayaran.")
        }
    }
    LaunchedEffect(chatState.cancelledConversationId) {
        chatState.cancelledConversationId?.let { id ->
            viewModel.onCancelledHandled()
            onCancelled?.invoke(id) ?: toast("Project dibatalkan.")
        }
    }

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
                RefreshableContent(onRefresh = viewModel::load, modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                }
                NegotiationActionBar(
                    onCancel = { showCancel = true },
                    cancelEnabled = !cancelled && !negotiation.isPaid,
                    onContinue = viewModel::continueProject,
                    continueEnabled = !cancelled && !negotiation.umkmAgreed && !negotiation.isPaid,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        CancelProjectDialog(
            visible = showCancel,
            onDismiss = { showCancel = false },
            onConfirm = { reason ->
                showCancel = false
                viewModel.cancel(reason)
            }
        )
    }
}

