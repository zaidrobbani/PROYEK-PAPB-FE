package com.example.proyek_papb_fe.feature.umkm.project_list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.UmkmDashboardRoute
import com.example.proyek_papb_fe.feature.umkm.project_list.container.ProjectListScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProjectListRoute

/** Tab Project: ditumpuk di atas Beranda tanpa salinan ganda. */
fun NavController.navigateToProjectList() {
    navigate(ProjectListRoute) {
        popUpTo(UmkmDashboardRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.projectListScreen(
    onNotificationsClick: () -> Unit,
    onCreateProject: () -> Unit,
    onEditProject: (projectId: String) -> Unit,
    onViewProgress: (projectId: String) -> Unit,
    onReviewResult: (projectId: String) -> Unit,
    onViewApplications: (projectId: String) -> Unit,
    onFinalFiles: (projectId: String) -> Unit,
    onOrderAgain: (projectId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit
) {
    composable<ProjectListRoute> {
        ProjectListScreen(
            onNotificationsClick = onNotificationsClick,
            onCreateProject = onCreateProject,
            onEditProject = onEditProject,
            onViewProgress = onViewProgress,
            onReviewResult = onReviewResult,
            onViewApplications = onViewApplications,
            onFinalFiles = onFinalFiles,
            onOrderAgain = onOrderAgain,
            onTabSelected = onTabSelected
        )
    }
}
