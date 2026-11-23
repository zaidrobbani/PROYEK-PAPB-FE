package com.example.proyek_papb_fe.feature.umkm.planreview.container

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.feature.umkm.planreview.component.PlanReviewScreen
import com.example.proyek_papb_fe.feature.umkm.planreview.component.PlanReviewUiState
import com.example.proyek_papb_fe.feature.umkm.planreview.component.ReviewMilestoneUi
import com.example.proyek_papb_fe.feature.umkm.planreview.viewmodel.PlanReviewViewModel

@Composable
fun PlanReviewContainer(
    projectId: String,
    onBack: () -> Unit,
    onApproved: (projectId: String) -> Unit,
    viewModel: PlanReviewViewModel = viewModel(
        factory = PlanReviewViewModel.factory(projectId, AppContainer.workflowRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showChangeDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.readyToPay) {
        if (uiState.readyToPay) {
            viewModel.onReadyToPayHandled()
            onApproved(projectId)
        }
    }

    val detail = uiState.detail
    val plan = uiState.plan
    if (detail == null || plan == null) {
        if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        return
    }
    val agreement = detail.agreement
    val freelancerName = detail.freelancer?.name ?: "Freelancer"
    val submittedBy = buildString {
        append("Diajukan $freelancerName")
        if (agreement != null) {
            val deadline = agreement.agreedDeadlineMillis?.let(::formatShortDate) ?: agreement.agreedDeadline
            append(", deadline $deadline (${agreement.durationDays} hari)")
        }
        when {
            detail.project.status != ProjectStatusCode.MENUNGGU_KESEPAKATAN -> append(" · ${detail.project.statusLabel}")
            !plan.isComplete -> append(" · Rencana belum lengkap (total bobot harus 100%)")
            agreement?.approvedByMe == true -> append(" · Kamu sudah menyetujui, menunggu freelancer")
            agreement?.changeRequestNote != null -> append(" · Permintaan ubah: ${agreement.changeRequestNote}")
        }
    }

    PlanReviewScreen(
        state = PlanReviewUiState(
            projectTitle = detail.project.title,
            submittedBy = submittedBy,
            milestones = plan.items.map { ReviewMilestoneUi(it.name, it.weightPercent.toInt()) }
        ),
        onBack = onBack,
        onRequestChanges = {
            if (uiState.canRequestChanges) showChangeDialog = true
            else Toast.makeText(context, "Rencana tidak dapat diminta ubah pada tahap ini.", Toast.LENGTH_SHORT).show()
        },
        onApprove = {
            when {
                uiState.isSubmitting -> Unit
                uiState.canApprove -> viewModel.approve()
                agreement?.approvedByMe == true ->
                    Toast.makeText(context, "Kamu sudah menyetujui rencana ini. Menunggu freelancer.", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(context, "Rencana belum bisa disetujui pada tahap ini.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    if (showChangeDialog) {
        var note by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangeDialog = false },
            title = { Text("Minta ubah rencana") },
            text = {
                OutlinedTextField(
                    value = note,
                    onValueChange = { if (it.length <= 2000) note = it },
                    placeholder = { Text("Tulis bagian yang perlu diubah freelancer") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    enabled = note.isNotBlank(),
                    onClick = {
                        showChangeDialog = false
                        viewModel.requestChanges(note)
                    }
                ) { Text("Kirim") }
            },
            dismissButton = { TextButton(onClick = { showChangeDialog = false }) { Text("Batal") } }
        )
    }
}
