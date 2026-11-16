package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.container.NegotiationScreen
import kotlinx.serialization.Serializable

/** Membawa id percakapan; negosiasi dan pesannya dibaca dari store lokal yang sama. */
@Serializable
data class UmkmNegotiationRoute(val conversationId: String)

fun NavController.navigateToUmkmNegotiation(conversationId: String) {
    navigate(UmkmNegotiationRoute(conversationId)) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmNegotiationScreen(
    onBack: () -> Unit,
    onCancelled: ((conversationId: String) -> Unit)?,
    onBothAgreed: (() -> Unit)?
) {
    composable<UmkmNegotiationRoute> { entry ->
        NegotiationScreen(
            conversationId = entry.toRoute<UmkmNegotiationRoute>().conversationId,
            onBack = onBack,
            onCancelled = onCancelled,
            onBothAgreed = onBothAgreed
        )
    }
}
