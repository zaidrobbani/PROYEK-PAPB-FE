package com.example.proyek_papb_fe.feature.umkm.profile.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.umkm.profile.viewmodel.UmkmProfileViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmBottomNav
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTopBar
import com.example.proyek_papb_fe.feature.umkm.data.PaymentStatus
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotificationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.formatRupiahCompact
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileHeaderCard
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileMenuItem
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileSectionTitle
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileStat
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileStatsRow

@Composable
fun UmkmProfileScreen(
    onNotificationsClick: () -> Unit,
    onManageBusinessProfile: (() -> Unit)?,
    onOpenProjects: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSecurity: (() -> Unit)?,
    onLogout: () -> Unit,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UmkmProfileViewModel = viewModel(
        factory = UmkmProfileViewModel.factory(
            AppContainer.authRepository,
            AppContainer.profileRepository,
            AppContainer.workflowRepository,
            AppContainer.paymentRepository,
            AppContainer.notificationRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    val context = LocalContext.current
    fun comingSoon(feature: String): () -> Unit = {
        Toast.makeText(context, "$feature belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
    }

    val profile = UmkmAccount.profile
    val stats = listOf(
        ProfileStat(uiState.projectCount.toString(), "Project dibuat"),
        ProfileStat(uiState.completedCount.toString(), "Selesai"),
        ProfileStat(formatRupiahCompact(uiState.paidTotal), "Total transaksi")
    )
    val attention = uiState.attentionCount
    val unread = uiState.unreadNotifications

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UmkmTopBar(onNotificationsClick = onNotificationsClick, title = "Profil")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 84.dp - 64.dp, bottom = 110.dp - 72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") { ProfileHeaderCard(profile) }
            item(key = "stats") { ProfileStatsRow(stats) }
            item(key = "section") { ProfileSectionTitle("Kelola Akun") }
            item(key = "business") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_storefront,
                    title = "Kelola Profil Usaha",
                    subtitle = if (UmkmAccount.hasBusinessProfile) "Nama usaha, kategori, dan alamat"
                    else "Belum diisi. Wajib sebelum membuat project",
                    onClick = onManageBusinessProfile ?: comingSoon("Kelola Profil Usaha")
                )
            }
            item(key = "projects") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_folder_open,
                    title = "Project Saya",
                    subtitle = "${uiState.projectCount} project, $attention perlu perhatian",
                    onClick = onOpenProjects
                )
            }
            item(key = "transactions") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_receipt_long,
                    title = "Riwayat Transaksi",
                    subtitle = "Pembayaran dan bukti bayar",
                    onClick = onOpenTransactions
                )
            }
            item(key = "notifications") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_notifications,
                    title = "Notifikasi",
                    subtitle = if (unread > 0) "$unread belum dibaca" else "Semua sudah dibaca",
                    onClick = onOpenNotifications
                )
            }
            item(key = "security") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_shield,
                    title = "Keamanan & Kata Sandi",
                    subtitle = "Ubah kata sandi akun",
                    onClick = onSecurity ?: comingSoon("Keamanan & Kata Sandi")
                )
            }
            item(key = "logout") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_logout,
                    title = "Keluar",
                    subtitle = "Akhiri sesi di perangkat ini",
                    onClick = onLogout,
                    destructive = true
                )
            }
        }
        UmkmBottomNav(
            selectedTab = UmkmTab.Profil,
            onTabSelected = { tab -> if (tab != UmkmTab.Profil) onTabSelected(tab) }
        )
    }
}
