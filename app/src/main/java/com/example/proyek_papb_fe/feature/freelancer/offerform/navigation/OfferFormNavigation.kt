package com.example.proyek_papb_fe.feature.freelancer.offerform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.offerform.container.OfferFormContainer
import kotlinx.serialization.Serializable

/** Figma: Form Pengajuan Freelancer (17:4405) — "Ajukan Penawaran". */
@Serializable
data class OfferFormRoute(val projectId: Int)

fun NavController.navigateToOfferForm(projectId: Int) {
    navigate(OfferFormRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.offerFormScreen(
    onBack: () -> Unit,
    /** Penawaran lolos validasi dan terkirim; tujuan berikutnya: Status Pengajuan. */
    onOfferSubmitted: (projectId: Int) -> Unit
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
