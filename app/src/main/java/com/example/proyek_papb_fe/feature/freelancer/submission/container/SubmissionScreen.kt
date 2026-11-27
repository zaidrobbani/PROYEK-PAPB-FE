package com.example.proyek_papb_fe.feature.freelancer.submission.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalFilterOption
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionListScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.viewmodel.ProposalListViewModel

@Composable
fun SubmissionScreen(
    onProposalClick: (projectId: String) -> Unit,
    onFindProjectsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    viewModel: ProposalListViewModel = viewModel(
        factory = ProposalListViewModel.factory(AppContainer.projectRepository, AppContainer.workflowRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedFilter by rememberSaveable { mutableStateOf(ProjectFilter.SEMUA) }
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    val allItems = uiState.items
    val filterOptions = ProjectFilter.entries.map { filter ->
        ProposalFilterOption(filter = filter, count = allItems.count { filter.matches(it.stage.status) })
    }

    SubmissionListScreen(
        onRefresh = viewModel::refresh,
        filterOptions = filterOptions,
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
        proposals = allItems.filter { selectedFilter.matches(it.stage.status) },
        onProposalClick = onProposalClick,
        onFindProjectsClick = onFindProjectsClick,
        onNotificationClick = onNotificationClick
    )
}
