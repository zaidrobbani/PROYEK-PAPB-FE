package com.example.proyek_papb_fe.feature.freelancer.negotiation.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.data.model.NegotiationMessage
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.core.util.formatTime
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.CancelProjectBottomSheet
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationParty
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationScreen
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationUiState
import com.example.proyek_papb_fe.feature.freelancer.negotiation.viewmodel.FreelancerNegotiationViewModel

private val DefaultCancellationReason = CancellationReason.PRICE_MISMATCH

@Composable
fun NegotiationContainer(
    projectId: String,
    onBack: () -> Unit,
    onProjectCancelled: (projectId: String) -> Unit,
    onContinueProject: (projectId: String) -> Unit,
    viewModel: FreelancerNegotiationViewModel = viewModel(
        factory = FreelancerNegotiationViewModel.factory(projectId, AppContainer.workflowRepository, AppContainer.chatRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isCancelSheetVisible by rememberSaveable(projectId) { mutableStateOf(false) }
    var selectedReason by rememberSaveable(projectId) { mutableStateOf(DefaultCancellationReason) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.cancelled) {
        if (uiState.cancelled) {
            viewModel.onCancelledHandled()
            onProjectCancelled(projectId)
        }
    }

    val detail = uiState.detail
    if (detail == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Negosiasi", onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }
    if (detail.project.status == ProjectStatusCode.DIBATALKAN) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Negosiasi", onBack = onBack)
            ErrorContent(
                message = "Negosiasi untuk proyek ini sudah dibatalkan" +
                    (detail.cancellation?.reasonLabel?.let { " ($it)" } ?: "") + " dan tidak dapat dilanjutkan.",
                onRetry = viewModel::load
            )
        }
        return
    }

    val agreement = detail.agreement
    val umkm = detail.umkm
    val myId = AppContainer.currentUserId
    val state = NegotiationUiState(
        projectTitle = detail.project.title,
        client = NegotiationParty(
            initials = initialsOf(umkm.name),
            name = umkm.subtitle ?: umkm.name,
            role = "${umkm.name}, UMKM"
        ),
        freelancer = NegotiationParty(
            initials = initialsOf(detail.freelancer?.name.orEmpty()),
            name = detail.freelancer?.name ?: "Freelancer",
            role = "Freelancer"
        ),
        projectValue = rupiah(agreement?.agreedPrice ?: detail.project.budget),
        duration = "${agreement?.durationDays ?: 0} hari kalender",
        output = agreement?.scopeOfWork ?: detail.project.description,
        paymentMethod = agreement?.paymentMethodLabel ?: "QRIS (simulasi)",
        dayLabel = "Riwayat pesan",
        messages = uiState.messages.map { message ->
            NegotiationMessage(
                id = message.id,
                sender = if (message.senderUserId == myId) MessageSender.FREELANCER else MessageSender.CLIENT,
                text = message.previewText,
                time = formatTime(message.sentAtMillis)
            )
        }
    )

    NegotiationScreen(
        onRefresh = viewModel::refresh,
        state = state,
        onBack = onBack,
        onCancelClick = {
            if (uiState.canCancel) isCancelSheetVisible = true
            else Toast.makeText(context, "Proyek sudah dibayar dan resmi dikerjakan, tidak dapat dibatalkan.", Toast.LENGTH_SHORT).show()
        },
        onContinueClick = { onContinueProject(projectId) }
    )

    if (isCancelSheetVisible) {
        CancelProjectBottomSheet(
            selectedReason = selectedReason,
            onReasonSelected = { selectedReason = it },
            onConfirm = {
                isCancelSheetVisible = false
                viewModel.cancel(selectedReason)
            },
            onDismiss = {
                isCancelSheetVisible = false
                selectedReason = DefaultCancellationReason
            }
        )
    }
}
