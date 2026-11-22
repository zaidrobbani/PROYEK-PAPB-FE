package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.container.CompareDraftsScreen
import kotlinx.serialization.Serializable

@Serializable
data class UmkmCompareDraftsRoute(val conversationId: String, val draftMessageId: String)

fun NavController.navigateToUmkmCompareDrafts(conversationId: String, draftMessageId: String) {
    navigate(UmkmCompareDraftsRoute(conversationId, draftMessageId)) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmCompareDraftsScreen(
    onBack: () -> Unit,
    onOpenChat: (conversationId: String) -> Unit,
    onApproveFinal: (() -> Unit)?
) {
    composable<UmkmCompareDraftsRoute> { entry ->
        val route = entry.toRoute<UmkmCompareDraftsRoute>()
        CompareDraftsScreen(
            conversationId = route.conversationId,
            draftMessageId = route.draftMessageId,
            onBack = onBack,
            onOpenChat = { onOpenChat(route.conversationId) },
            onApproveFinal = onApproveFinal
        )
    }
}
