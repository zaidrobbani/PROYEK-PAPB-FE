package com.example.proyek_papb_fe.feature.umkm.business_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.business_profile.container.BusinessProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object UmkmBusinessProfileRoute

fun NavController.navigateToUmkmBusinessProfile() {
    navigate(UmkmBusinessProfileRoute) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmBusinessProfileScreen(onBack: () -> Unit) {
    composable<UmkmBusinessProfileRoute> {
        BusinessProfileScreen(onBack = onBack)
    }
}
