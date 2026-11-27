package com.example.proyek_papb_fe.feature.freelancer.chat.navigation

import com.example.proyek_papb_fe.core.navigation.TopLevelRoute
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.freelancer.chat.container.ChatRoomContainer
import com.example.proyek_papb_fe.feature.freelancer.chat.container.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
data object ChatRoute : TopLevelRoute

fun NavGraphBuilder.chatScreen(
    onConversationClick: (conversationId: String) -> Unit,
    onNotificationClick: () -> Unit
) {
    composable<ChatRoute> { backStackEntry ->
        KeepSizeWhileLeaving(entry = backStackEntry) {
            ChatScreen(onConversationClick = onConversationClick, onNotificationClick = onNotificationClick)
        }
    }
}

@Serializable
data class ChatRoomRoute(val conversationId: String)

fun NavController.navigateToChatRoom(conversationId: String) {
    navigate(ChatRoomRoute(conversationId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.chatRoomScreen(
    onBack: () -> Unit,
    onProjectDetailClick: (projectId: String) -> Unit,
    onContinueProject: (projectId: String) -> Unit,
    onProjectCancelled: (projectId: String) -> Unit,
    onOpenResultStage: (projectId: String, stage: ResultReviewStage) -> Unit
) {
    composable<ChatRoomRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ChatRoomRoute>()
        ChatRoomContainer(
            conversationId = route.conversationId,
            onBack = onBack,
            onProjectDetailClick = onProjectDetailClick,
            onContinueProject = onContinueProject,
            onProjectCancelled = onProjectCancelled,
            onOpenResultStage = onOpenResultStage
        )
    }
}
