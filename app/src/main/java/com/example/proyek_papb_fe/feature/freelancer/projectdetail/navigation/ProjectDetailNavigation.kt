package com.example.proyek_papb_fe.feature.freelancer.projectdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.container.ProjectDetailContainer
import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetailRoute(val projectId: Int)

fun NavController.navigateToProjectDetail(projectId: Int) {
    navigate(ProjectDetailRoute(projectId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.projectDetailScreen(
    onBack: () -> Unit,
    /** Tombol Chat: buka ruang chat dengan UMKM untuk project ini. */
    onChatClick: (projectId: Int) -> Unit = {},
    onSubmitOfferClick: (projectId: Int) -> Unit = {}
) {
    composable<ProjectDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ProjectDetailRoute>()
        ProjectDetailContainer(
            projectId = route.projectId,
            onBack = onBack,
            onChatClick = { onChatClick(route.projectId) },
            onSubmitOfferClick = { onSubmitOfferClick(route.projectId) }
        )
    }
}
