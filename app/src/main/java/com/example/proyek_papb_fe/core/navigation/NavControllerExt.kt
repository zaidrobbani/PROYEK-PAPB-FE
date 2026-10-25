package com.example.proyek_papb_fe.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Pindah antar tab bottom bar tanpa menumpuk back stack,
 * dan state tiap tab (misal posisi scroll) tetap tersimpan.
 */
fun NavController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
