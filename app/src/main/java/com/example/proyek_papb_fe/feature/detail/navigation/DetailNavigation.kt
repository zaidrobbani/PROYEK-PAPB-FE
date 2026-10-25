package com.example.proyek_papb_fe.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.detail.container.DetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(val itemId: Int)

fun NavController.navigateToDetail(itemId: Int) {
    navigate(DetailRoute(itemId))
}

fun NavGraphBuilder.detailScreen(onBack: () -> Unit) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        DetailScreen(itemId = route.itemId, onBack = onBack)
    }
}
