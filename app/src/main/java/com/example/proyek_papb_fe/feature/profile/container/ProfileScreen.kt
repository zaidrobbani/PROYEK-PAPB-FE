package com.example.proyek_papb_fe.feature.profile.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.NotificationStore
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.feature.profile.component.ProfileContent
import com.example.proyek_papb_fe.feature.profile.component.ProfileMenu
import com.example.proyek_papb_fe.feature.profile.component.ProfileStat
import com.example.proyek_papb_fe.feature.profile.component.ProfileUiState
import java.util.Locale

/** 2850000 → "Rp 2.850.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

private fun FreelancerProfile.toUiState(
    availableBalance: Long,
    portfolioCount: Int,
    workExperienceCount: Int,
    unreadNotificationCount: Int
) = ProfileUiState(
    initials = initials,
    photoUri = photoUri,
    name = fullName,
    headline = headline,
    city = city,
    ratingLabel = "${String.format(Locale.US, "%.1f", rating)}, $reviewCount ulasan",
    isAvailableForWork = isAvailableForWork,
    stats = listOf(
        ProfileStat("$completedProjects", "Proyek selesai"),
        ProfileStat("$trustPercent%", "Amanah"),
        ProfileStat(experienceYearsLabel, "Pengalaman")
    ),
    skills = skills,
    menuSubtitles = mapOf(
        ProfileMenu.PROFESSIONAL_PROFILE to "Biodata, tarif, dan area layanan",
        ProfileMenu.PORTFOLIO to "$portfolioCount karya ditampilkan",
        ProfileMenu.EXPERIENCE to "$workExperienceCount pengalaman kerja",
        ProfileMenu.WALLET to "Saldo tersedia ${formatRupiah(availableBalance)}",
        ProfileMenu.NOTIFICATIONS to if (unreadNotificationCount > 0) "$unreadNotificationCount belum dibaca" else "Semua sudah dibaca",
        ProfileMenu.LOGOUT to "Akhiri sesi di perangkat ini"
    ),
    hasUnreadNotification = unreadNotificationCount > 0
)

/**
 * Container tab "Profil" (Figma 27:250). Data dari [FreelancerProfileStore],
 * [NotificationStore], dan [WalletStore], sehingga perubahan di layar Kelola langsung
 * tampil di sini. Menu membuka Kelola Profil Profesional, Kelola Portofolio, Kelola
 * Pengalaman, Dompet & Rekening (27:80), dan Notifikasi; lonceng juga membuka Notifikasi
 * dan "Kelola" di Keahlian membuka Kelola Profil Profesional. "Keluar" belum ada di aplikasi.
 */
@Composable
fun ProfileScreen(
    onProfessionalProfileClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    onExperienceClick: () -> Unit,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state = FreelancerProfileStore.profile.toUiState(
        availableBalance = WalletStore.summary.availableBalance,
        portfolioCount = FreelancerProfileStore.portfolio.size,
        workExperienceCount = FreelancerProfileStore.experiences.size,
        unreadNotificationCount = NotificationStore.unreadCount
    )

    ProfileContent(
        state = state,
        onNotificationClick = onNotificationsClick,
        onManageSkillsClick = onProfessionalProfileClick,
        onMenuClick = { menu ->
            when (menu) {
                ProfileMenu.PROFESSIONAL_PROFILE -> onProfessionalProfileClick()
                ProfileMenu.PORTFOLIO -> onPortfolioClick()
                ProfileMenu.EXPERIENCE -> onExperienceClick()
                ProfileMenu.WALLET -> onWalletClick()
                ProfileMenu.NOTIFICATIONS -> onNotificationsClick()
                ProfileMenu.LOGOUT -> Toast.makeText(context, "${menu.title} segera hadir", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 843)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onProfessionalProfileClick = {}, onPortfolioClick = {}, onExperienceClick = {}, onWalletClick = {}, onNotificationsClick = {})
}
