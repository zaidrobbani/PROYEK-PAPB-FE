package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.container.ChatRoomScreen
import kotlinx.serialization.Serializable

@Serializable
data class UmkmChatRoomRoute(val conversationId: String)

fun NavController.navigateToUmkmChatRoom(conversationId: String) {
    navigate(UmkmChatRoomRoute(conversationId)) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmChatRoomScreen(
    onBack: () -> Unit,
    onOpenProject: ((projectId: String) -> Unit)?,
    onCompareDrafts: ((conversationId: String, draftMessageId: String) -> Unit)?
) {
    composable<UmkmChatRoomRoute> { entry ->
        ChatRoomScreen(
            conversationId = entry.toRoute<UmkmChatRoomRoute>().conversationId,
            onBack = onBack,
            onOpenProject = onOpenProject,
            onCompareDrafts = onCompareDrafts
        )
    }
}
