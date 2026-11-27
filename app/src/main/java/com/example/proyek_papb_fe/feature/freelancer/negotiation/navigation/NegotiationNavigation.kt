package com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.negotiation.container.NegotiationContainer
import kotlinx.serialization.Serializable

@Serializable
data class NegotiationRoute(val projectId: String)

fun NavController.navigateToNegotiation(projectId: String) {
    navigate(NegotiationRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.negotiationScreen(
    onBack: () -> Unit,

    onProjectCancelled: (projectId: String) -> Unit,
    onContinueProject: (projectId: String) -> Unit
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
