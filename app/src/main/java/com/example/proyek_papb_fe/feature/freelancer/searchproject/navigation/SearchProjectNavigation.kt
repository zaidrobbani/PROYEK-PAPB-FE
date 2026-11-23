package com.example.proyek_papb_fe.feature.freelancer.searchproject.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.freelancer.searchproject.container.SearchProjectContainer
import kotlinx.serialization.Serializable

@Serializable
data object SearchProjectRoute

fun NavGraphBuilder.searchProjectScreen(
    onProjectClick: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    composable<SearchProjectRoute> { backStackEntry ->
        KeepSizeWhileLeaving(entry = backStackEntry) {
            SearchProjectContainer(
                onProjectClick = onProjectClick,
                onFilterClick = onFilterClick,
                onNotificationClick = onNotificationClick
            )
        }
    }
}
