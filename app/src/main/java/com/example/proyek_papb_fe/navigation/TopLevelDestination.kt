package com.example.proyek_papb_fe.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.proyek_papb_fe.feature.home.navigation.HomeRoute
import com.example.proyek_papb_fe.feature.profile.navigation.ProfileRoute

/** Daftar tab yang tampil di bottom bar. Tambah tab baru cukup di sini. */
enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: Any
) {
    HOME(label = "Beranda", icon = Icons.Filled.Home, route = HomeRoute),
    PROFILE(label = "Profil", icon = Icons.Filled.Person, route = ProfileRoute)
}

fun NavDestination?.isTopLevelDestination(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
