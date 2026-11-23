package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.navigation.UmkmNegotiationRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.container.ProjectCancelledScreen
import kotlinx.serialization.Serializable

@Serializable
data class UmkmProjectCancelledRoute(val conversationId: String)

fun NavController.navigateToUmkmProjectCancelled(conversationId: String) {
    navigate(UmkmProjectCancelledRoute(conversationId)) {
        popUpTo<UmkmNegotiationRoute> { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.umkmProjectCancelledScreen(
    onBackToProjects: () -> Unit,
    onFindOtherOptions: () -> Unit
) {
    composable<UmkmProjectCancelledRoute> { entry ->
        ProjectCancelledScreen(
            conversationId = entry.toRoute<UmkmProjectCancelledRoute>().conversationId,
            onBackToProjects = onBackToProjects,
            onFindOtherOptions = onFindOtherOptions
        )
    }
}
