package com.example.proyek_papb_fe.feature.umkm.notification_detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.data.NotificationTarget
import com.example.proyek_papb_fe.feature.umkm.notification_detail.container.NotificationDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class UmkmNotificationDetailRoute(val notificationId: String)

fun NavController.navigateToUmkmNotificationDetail(notificationId: String) {
    navigate(UmkmNotificationDetailRoute(notificationId)) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmNotificationDetailScreen(
    onBack: () -> Unit,
    onOpenTarget: (NotificationTarget) -> Unit
) {
    composable<UmkmNotificationDetailRoute> { entry ->
        NotificationDetailScreen(
            notificationId = entry.toRoute<UmkmNotificationDetailRoute>().notificationId,
            onBack = onBack,
            onOpenTarget = onOpenTarget
        )
    }
}
