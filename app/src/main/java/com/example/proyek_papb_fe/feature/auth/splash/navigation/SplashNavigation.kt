package com.example.proyek_papb_fe.feature.auth.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.splash.container.SplashScreen
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(
    onStartClick: () -> Unit,
    onLoggedIn: (UserRole) -> Unit
) {
    composable<SplashRoute> {
        SplashScreen(onStartClick = onStartClick, onLoggedIn = onLoggedIn)
    }
}
