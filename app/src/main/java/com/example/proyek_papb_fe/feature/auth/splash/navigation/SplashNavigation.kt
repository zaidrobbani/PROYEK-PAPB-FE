package com.example.proyek_papb_fe.feature.auth.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.splash.container.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(onStartClick: () -> Unit) {
    composable<SplashRoute> {
        SplashScreen(onStartClick = onStartClick)
    }
}
