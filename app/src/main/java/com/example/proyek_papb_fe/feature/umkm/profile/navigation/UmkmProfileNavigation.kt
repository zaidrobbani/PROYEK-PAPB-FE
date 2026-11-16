package com.example.proyek_papb_fe.feature.umkm.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.login.navigation.LoginRoute
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.UmkmDashboardRoute
import com.example.proyek_papb_fe.feature.umkm.profile.container.UmkmProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object UmkmProfileRoute

/** Tab Profil: ditumpuk di atas Beranda tanpa salinan ganda, sama seperti tab UMKM lain. */
fun NavController.navigateToUmkmProfile() {
    navigate(UmkmProfileRoute) {
        popUpTo(UmkmDashboardRoute)
        launchSingleTop = true
    }
}

/**
 * Simulasi logout frontend (Blueprint TK-F-U-27): belum ada sesi/token maupun API logout,
 * jadi "mengakhiri sesi" berarti membuang seluruh back stack lalu membuka Login, sehingga
 * tombol Back tidak bisa kembali ke halaman UMKM. Data dummy lokal tidak dihapus.
 */
fun NavController.logoutToLogin() {
    navigate(LoginRoute) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.umkmProfileScreen(
    onNotificationsClick: () -> Unit,
    onManageBusinessProfile: (() -> Unit)?,
    onOpenProjects: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSecurity: (() -> Unit)?,
    onLogout: () -> Unit,
    onTabSelected: (UmkmTab) -> Unit
) {
    composable<UmkmProfileRoute> {
        UmkmProfileScreen(
            onNotificationsClick = onNotificationsClick,
            onManageBusinessProfile = onManageBusinessProfile,
            onOpenProjects = onOpenProjects,
            onOpenTransactions = onOpenTransactions,
            onOpenNotifications = onOpenNotifications,
            onSecurity = onSecurity,
            onLogout = onLogout,
            onTabSelected = onTabSelected
        )
    }
}
