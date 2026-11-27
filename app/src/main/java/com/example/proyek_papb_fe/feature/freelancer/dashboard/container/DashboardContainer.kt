package com.example.proyek_papb_fe.feature.freelancer.dashboard.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardScreen
import com.example.proyek_papb_fe.feature.freelancer.dashboard.viewmodel.FreelancerDashboardViewModel

@Composable
fun DashboardContainer(
    onWalletClick: () -> Unit = {},
    onActiveProjectClick: (projectId: String) -> Unit = {},
    onSeeAllActiveProjectsClick: () -> Unit = {},
    onRecommendedProjectClick: (projectId: String) -> Unit = {},
    onSeeAllRecommendationsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onManageSkillsClick: () -> Unit = {},
    viewModel: FreelancerDashboardViewModel = viewModel(
        factory = FreelancerDashboardViewModel.factory(
            AppContainer.authRepository,
            AppContainer.paymentRepository,
            AppContainer.workflowRepository,
            AppContainer.projectRepository,
            AppContainer.masterDataRepository,
            AppContainer.notificationRepository,
            AppContainer.profileRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    DashboardScreen(
        onRefresh = viewModel::refresh,
        userName = uiState.userName,
        availableBalance = rupiah(uiState.availableBalance),
        activeProject = uiState.activeProject,
        recommendations = uiState.recommendations,
        onWalletClick = onWalletClick,
        onActiveProjectClick = onActiveProjectClick,
        onProjectClick = onRecommendedProjectClick,
        hasUnreadNotification = uiState.hasUnreadNotification,
        onNotificationClick = onNotificationClick,
        onSeeAllActiveProjectsClick = onSeeAllActiveProjectsClick,
        onSeeAllRecommendationsClick = onSeeAllRecommendationsClick,
        hasSkills = uiState.hasSkills,
        isLoading = uiState.isLoading,
        onManageSkillsClick = onManageSkillsClick
    )
}
