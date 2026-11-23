package com.example.proyek_papb_fe.feature.freelancer.revision.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.freelancer.revision.component.RevisionRequestScreen
import com.example.proyek_papb_fe.feature.freelancer.revision.viewmodel.RevisionRequestViewModel

@Composable
fun RevisionRequestContainer(
    projectId: String,
    onBack: () -> Unit,
    onOpenProjectChat: (projectId: String) -> Unit,
    viewModel: RevisionRequestViewModel = viewModel(
        factory = RevisionRequestViewModel.factory(projectId, AppContainer.submissionRepository, AppContainer.workflowRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val request = uiState.request
    if (request == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Permintaan Revisi", onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }
    RevisionRequestScreen(
        request = request,
        onBack = onBack,
        onUploadRevisedDraft = { onOpenProjectChat(projectId) },
        onOpenChat = { onOpenProjectChat(projectId) },
        onAskClarification = { onOpenProjectChat(projectId) },
        onOpenReferenceFile = {
            Toast.makeText(context, "Pratinjau ${request.referenceFileName} belum tersedia", Toast.LENGTH_SHORT).show()
        }
    )
}
