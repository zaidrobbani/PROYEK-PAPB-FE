package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.container.ConversationListScreen
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.UmkmDashboardRoute
import kotlinx.serialization.Serializable

@Serializable
data object UmkmConversationListRoute

fun NavController.navigateToUmkmConversationList() {
    navigate(UmkmConversationListRoute) {
        popUpTo(UmkmDashboardRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.umkmConversationListScreen(
    onNotificationsClick: () -> Unit,
    onOpenConversation: ((conversationId: String) -> Unit)?,
    onTabSelected: (UmkmTab) -> Unit
) {
    composable<UmkmConversationListRoute> {
        ConversationListScreen(
            onNotificationsClick = onNotificationsClick,
            onOpenConversation = onOpenConversation,
            onTabSelected = onTabSelected
        )
    }
}
