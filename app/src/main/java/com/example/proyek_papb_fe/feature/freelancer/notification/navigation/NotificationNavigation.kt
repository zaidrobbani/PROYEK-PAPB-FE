package com.example.proyek_papb_fe.feature.freelancer.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.notification.container.NotificationContainer
import com.example.proyek_papb_fe.feature.freelancer.revision.container.RevisionRequestContainer
import kotlinx.serialization.Serializable

@Serializable
data object NotificationRoute

@Serializable
data class RevisionRequestRoute(val projectId: String)

fun NavController.navigateToNotifications() = navigate(NotificationRoute) { launchSingleTop = true }

fun NavController.navigateToRevisionRequest(projectId: String) = navigate(RevisionRequestRoute(projectId)) { launchSingleTop = true }

fun NavGraphBuilder.notificationScreen(
    onBack: () -> Unit,
    onRevisionRequestClick: (projectId: String) -> Unit,
    onMessageClick: (conversationId: String) -> Unit,
    onWalletClick: () -> Unit,
    onProfessionalProfileClick: () -> Unit,
    onFindProjectsClick: () -> Unit,
    onProjectClick: (projectId: String) -> Unit
) {
    composable<NotificationRoute> {
        NotificationContainer(
            onBack = onBack,
            onRevisionRequestClick = onRevisionRequestClick,
            onMessageClick = onMessageClick,
            onWalletClick = onWalletClick,
            onProfessionalProfileClick = onProfessionalProfileClick,
            onFindProjectsClick = onFindProjectsClick,
            onProjectClick = onProjectClick
        )
    }
}

fun NavGraphBuilder.revisionRequestScreen(
    onBack: () -> Unit,
    onOpenProjectChat: (projectId: String) -> Unit
) {
    composable<RevisionRequestRoute> { entry ->
        RevisionRequestContainer(
            projectId = entry.toRoute<RevisionRequestRoute>().projectId,
            onBack = onBack,
            onOpenProjectChat = onOpenProjectChat
        )
    }
}
