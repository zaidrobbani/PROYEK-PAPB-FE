package com.example.proyek_papb_fe.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.home.container.HomeContainer
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(onItemClick: (Int) -> Unit) {
    composable<HomeRoute> {
        HomeContainer(onItemClick = onItemClick)
    }
}
