package com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.negotiation.container.NegotiationContainer
import kotlinx.serialization.Serializable

/** Figma: Negosiasi (Freelancer) (26:2), ruang negosiasi satu project. */
@Serializable
data class NegotiationRoute(val projectId: Int)

fun NavController.navigateToNegotiation(projectId: Int) {
    navigate(NegotiationRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.negotiationScreen(
    onBack: () -> Unit,
    /**
     * Project sudah dibatalkan lewat "Modal Batalkan Project (Freelancer)" (26:87)
     * dan statusnya tersimpan; tinggal navigasi keluar dari Negosiasi.
     */
    onProjectCancelled: (projectId: Int) -> Unit,
    /** Figma "Rencana Pengerjaan (Freelancer)" (88:2). */
    onContinueProject: (projectId: Int) -> Unit
) {
    composable<NegotiationRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<NegotiationRoute>()
        NegotiationContainer(
            projectId = route.projectId,
            onBack = onBack,
            onProjectCancelled = onProjectCancelled,
            onContinueProject = onContinueProject
        )
    }
}
