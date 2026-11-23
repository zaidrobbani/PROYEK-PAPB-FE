package com.example.proyek_papb_fe.feature.freelancer.projectdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.container.ProjectDetailContainer
import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetailRoute(val projectId: String)

fun NavController.navigateToProjectDetail(projectId: String) {
    navigate(ProjectDetailRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.projectDetailScreen(
    onBack: () -> Unit,
    onChatClick: (conversationId: String) -> Unit = {},
    onSubmitOfferClick: (projectId: String) -> Unit = {},
    onViewMyOffer: (projectId: String) -> Unit = {}
) {
    composable<ProjectDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ProjectDetailRoute>()
        ProjectDetailContainer(
            projectId = route.projectId,
            onBack = onBack,
            onChatClick = onChatClick,
            onSubmitOfferClick = { onSubmitOfferClick(route.projectId) },
            onViewMyOffer = { onViewMyOffer(route.projectId) }
        )
    }
}
