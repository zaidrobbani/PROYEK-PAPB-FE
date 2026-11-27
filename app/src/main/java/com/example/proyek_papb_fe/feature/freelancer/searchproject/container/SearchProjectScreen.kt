package com.example.proyek_papb_fe.feature.freelancer.searchproject.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.SearchProjectScreen
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.buildCategoryOptions
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.filterProjects
import com.example.proyek_papb_fe.feature.freelancer.searchproject.viewmodel.SearchProjectViewModel

@Composable
fun SearchProjectContainer(
    onProjectClick: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: SearchProjectViewModel = viewModel(
        factory = SearchProjectViewModel.factory(
            AppContainer.projectRepository,
            AppContainer.masterDataRepository,
            AppContainer.notificationRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedCategory by rememberSaveable { mutableStateOf<ProjectCategory?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    val categories = remember(uiState.projects) { buildCategoryOptions(uiState.projects) }
    val visibleProjects = remember(uiState.projects, selectedCategory) {
        uiState.projects.filterProjects(query = "", category = selectedCategory)
    }

    SearchProjectScreen(
        onRefresh = viewModel::refresh,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        categories = categories,
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it },
        projects = visibleProjects,
        onProjectClick = onProjectClick,
        activeFilterCount = if (selectedCategory != null) 1 else 0,
        onFilterClick = onFilterClick,
        hasUnreadNotification = uiState.hasUnreadNotification,
        onNotificationClick = onNotificationClick
    )
}
