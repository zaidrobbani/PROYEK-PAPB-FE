package com.example.proyek_papb_fe.feature.freelancer.offerform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.offerform.container.OfferFormContainer
import kotlinx.serialization.Serializable

@Serializable
data class OfferFormRoute(val projectId: String)

fun NavController.navigateToOfferForm(projectId: String) {
    navigate(OfferFormRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.offerFormScreen(
    onBack: () -> Unit,
    onOfferSubmitted: (projectId: String) -> Unit
) {
    composable<OfferFormRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<OfferFormRoute>()
        OfferFormContainer(
            projectId = route.projectId,
            onBack = onBack,
            onOfferSubmitted = onOfferSubmitted
        )
    }
}
