package com.example.proyek_papb_fe.feature.profile.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.feature.profile.component.ProfileContent
import com.example.proyek_papb_fe.feature.profile.component.ProfileMenu
import com.example.proyek_papb_fe.feature.profile.component.ProfileStat
import com.example.proyek_papb_fe.feature.profile.component.ProfileUiState
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.FreelancerProfileViewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.freelancerProfileViewModelFactory
import androidx.compose.runtime.getValue
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.notification.viewmodel.NotificationsViewModel

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

@Composable
fun ProfileScreen(
    onProfessionalProfileClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    onExperienceClick: () -> Unit,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FreelancerProfileViewModel = viewModel(factory = freelancerProfileViewModelFactory()),
    notificationsViewModel: NotificationsViewModel = viewModel(
        factory = NotificationsViewModel.factory(AppContainer.notificationRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notifications by notificationsViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.load()
        notificationsViewModel.load()
    }
    val state = FreelancerProfileStore.profile.toUiState(
        availableBalance = uiState.availableBalance,
        portfolioCount = FreelancerProfileStore.portfolio.size,
        workExperienceCount = FreelancerProfileStore.experiences.size,
        unreadNotificationCount = notifications.unreadCount
    )

    ProfileContent(
        onRefresh = {
            notificationsViewModel.refresh()
            viewModel.refresh()
        },
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
                ProfileMenu.LOGOUT -> onLogoutClick()
            }
        },
        modifier = modifier
    )
}
