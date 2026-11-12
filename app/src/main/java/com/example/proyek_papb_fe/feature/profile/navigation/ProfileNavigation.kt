package com.example.proyek_papb_fe.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.profile.container.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen(
    /** Menu "Kelola Profil Profesional" dan "Kelola" di Keahlian (Figma 534:961). */
    onProfessionalProfileClick: () -> Unit,
    /** Menu "Kelola Portofolio" (Figma 535:1081). */
    onPortfolioClick: () -> Unit,
    /** Menu "Kelola Pengalaman" (Figma 538:1235). */
    onExperienceClick: () -> Unit,
    /** Menu "Dompet & Rekening" (Figma 27:80). */
    onWalletClick: () -> Unit,
    /** Menu "Notifikasi" dan lonceng app bar (Figma 539:1407). */
    onNotificationsClick: () -> Unit
) {
    composable<ProfileRoute> { backStackEntry ->
        // Jaga posisi scroll saat membuka halaman detail (bottom bar hilang).
        KeepSizeWhileLeaving(entry = backStackEntry) {
            ProfileScreen(
                onProfessionalProfileClick = onProfessionalProfileClick,
                onPortfolioClick = onPortfolioClick,
                onExperienceClick = onExperienceClick,
                onWalletClick = onWalletClick,
                onNotificationsClick = onNotificationsClick
            )
        }
    }
}
