package com.example.proyek_papb_fe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.proyek_papb_fe.feature.detail.navigation.detailScreen
import com.example.proyek_papb_fe.feature.detail.navigation.navigateToDetail
import com.example.proyek_papb_fe.feature.home.navigation.HomeRoute
import com.example.proyek_papb_fe.feature.home.navigation.homeScreen
import com.example.proyek_papb_fe.feature.profile.navigation.profileScreen

/** Satu-satunya tempat semua halaman (feature) didaftarkan dan dihubungkan. */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        homeScreen(
            onItemClick = { itemId -> navController.navigateToDetail(itemId) }
        )
        detailScreen(
            onBack = { navController.navigateUp() }
        )
        profileScreen()
    }
}
