package com.example.proyek_papb_fe.feature.umkm.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotification
import com.example.proyek_papb_fe.feature.umkm.notifications.container.NotificationsScreen
import kotlinx.serialization.Serializable

@Serializable
data object UmkmNotificationsRoute

fun NavController.navigateToUmkmNotifications() {
    navigate(UmkmNotificationsRoute) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmNotificationsScreen(
    onBack: () -> Unit,
    onNotificationClick: (UmkmNotification) -> Unit
) {
    composable<UmkmNotificationsRoute> {
        NotificationsScreen(onBack = onBack, onNotificationClick = onNotificationClick)
    }
}
