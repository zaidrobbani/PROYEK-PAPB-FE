package com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.container.ProjectMonitoringScreen
import kotlinx.serialization.Serializable

@Serializable
data class ProjectMonitoringRoute(val projectId: String)

fun NavController.navigateToProjectMonitoring(projectId: String) {
    navigate(ProjectMonitoringRoute(projectId)) { launchSingleTop = true }
}

fun NavGraphBuilder.projectMonitoringScreen(
    onBack: () -> Unit,
    onChatFreelancer: ((conversationId: String) -> Unit)?,
    onOpenWorkPlan: ((planId: String) -> Unit)?
) {
    composable<ProjectMonitoringRoute> { entry ->
        ProjectMonitoringScreen(
            projectId = entry.toRoute<ProjectMonitoringRoute>().projectId,
            onBack = onBack,
            onChatFreelancer = onChatFreelancer,
            onOpenWorkPlan = onOpenWorkPlan
        )
    }
}
