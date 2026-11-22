package com.example.proyek_papb_fe.feature.freelancer.projectdetail.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.component.ProjectDetailScreen
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.viewmodel.ProjectDetailViewModel

@Composable
fun ProjectDetailContainer(
    projectId: String,
    onBack: () -> Unit,
    onChatClick: (conversationId: String) -> Unit = {},
    onSubmitOfferClick: () -> Unit = {},
    onViewMyOffer: () -> Unit = {},
    viewModel: ProjectDetailViewModel = viewModel(
        factory = ProjectDetailViewModel.factory(
            projectId,
            AppContainer.projectRepository,
            AppContainer.masterDataRepository,
            AppContainer.chatRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.chatConversationId) {
        uiState.chatConversationId?.let {
            viewModel.onChatOpened()
            onChatClick(it)
        }
    }

    val project = uiState.project
    if (project == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Detail Proyek", onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }

    ProjectDetailScreen(
        project = project,
        onBack = onBack,
        onChatClick = viewModel::openChat,
        onSubmitOfferClick = {
            when {
                uiState.alreadyApplied -> onViewMyOffer()
                !uiState.isOpen -> Toast.makeText(context, "Project ini sudah tidak menerima penawaran.", Toast.LENGTH_SHORT).show()
                else -> onSubmitOfferClick()
            }
        }
    )
}
