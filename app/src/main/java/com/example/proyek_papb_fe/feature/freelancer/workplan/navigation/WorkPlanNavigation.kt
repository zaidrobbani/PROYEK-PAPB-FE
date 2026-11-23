package com.example.proyek_papb_fe.feature.freelancer.workplan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.workplan.container.WorkPlanContainer
import kotlinx.serialization.Serializable

@Serializable
data class WorkPlanRoute(val projectId: String)

fun NavController.navigateToWorkPlan(projectId: String) {
    navigate(WorkPlanRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.workPlanScreen(
    onBack: () -> Unit,
    onOpenUmkmSimulation: (projectId: String) -> Unit,
    onOpenProjectChat: (projectId: String) -> Unit = {},
    onOpenWallet: () -> Unit = {}
) {
    composable<WorkPlanRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkPlanRoute>()
        WorkPlanContainer(
            projectId = route.projectId,
            onBack = onBack,
            onOpenUmkmSimulation = { onOpenUmkmSimulation(route.projectId) },
            onOpenProjectChat = { onOpenProjectChat(route.projectId) },
            onOpenWallet = onOpenWallet
        )
    }
}
