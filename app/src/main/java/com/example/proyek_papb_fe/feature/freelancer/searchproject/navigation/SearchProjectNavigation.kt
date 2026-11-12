package com.example.proyek_papb_fe.feature.freelancer.searchproject.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.freelancer.searchproject.container.SearchProjectContainer
import kotlinx.serialization.Serializable

@Serializable
data object SearchProjectRoute

fun NavGraphBuilder.searchProjectScreen(
    onProjectClick: (Int) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    composable<SearchProjectRoute> { backStackEntry ->
        // Jaga posisi scroll saat membuka Detail Project dari posisi paling bawah daftar.
        KeepSizeWhileLeaving(entry = backStackEntry) {
            SearchProjectContainer(
                onProjectClick = onProjectClick,
                onFilterClick = onFilterClick,
                onNotificationClick = onNotificationClick
            )
        }
    }
}
