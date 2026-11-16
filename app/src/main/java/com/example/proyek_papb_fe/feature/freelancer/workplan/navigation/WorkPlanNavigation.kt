package com.example.proyek_papb_fe.feature.freelancer.workplan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.workplan.container.WorkPlanContainer
import kotlinx.serialization.Serializable

/** Figma: Rencana Pengerjaan (Freelancer) (88:2), dibuka dari "Lanjut Project" di Negosiasi. */
@Serializable
data class WorkPlanRoute(val projectId: Int)

fun NavController.navigateToWorkPlan(projectId: Int) {
    navigate(WorkPlanRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.workPlanScreen(
    onBack: () -> Unit,
    /** Kartu simulasi sisi UMKM (belum ada aplikasi UMKM): Tinjau Rencana / Pembayaran / Bukti. */
    onOpenUmkmSimulation: (projectId: Int) -> Unit
) {
    composable<WorkPlanRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WorkPlanRoute>()
        WorkPlanContainer(
            projectId = route.projectId,
            onBack = onBack,
            onOpenUmkmSimulation = { onOpenUmkmSimulation(route.projectId) }
        )
    }
}
