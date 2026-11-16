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
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransactionStore
import com.example.proyek_papb_fe.feature.umkm.data.formatRupiahCompact
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileHeaderCard
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileMenuItem
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileSectionTitle
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileStat
import com.example.proyek_papb_fe.feature.umkm.profile.component.ProfileStatsRow

/**
 * Profil Saya UMKM (Blueprint TK-F-U-01 Kelola Profil, TK-F-U-27 Logout). Semua angka
 * dihitung dari DATA DUMMY lokal (project, transaksi, notifikasi), bukan dari server.
 *
 * [onManageBusinessProfile] dan [onSecurity] null = screen tujuan belum dibuat; menu tetap
 * memberi respons berupa pesan singkat.
 */
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun comingSoon(feature: String): () -> Unit = {
        Toast.makeText(context, "$feature belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
    }

    val profile = UmkmAccount.profile
    val projects = UmkmProjectStore.projects
    val paidTotal = UmkmTransactionStore.transactions
        .filter { it.paymentStatus == PaymentStatus.Berhasil }
        .sumOf { it.amount }
    val stats = listOf(
        ProfileStat(projects.size.toString(), "Project dibuat"),
        ProfileStat(projects.count { it.status == ProjectStatus.Selesai }.toString(), "Selesai"),
        ProfileStat(formatRupiahCompact(paidTotal), "Total transaksi")
    )
    val attention = projects.count { UmkmProjectStore.needsAction(it) }
    val unread = UmkmNotificationStore.unreadCount

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
            // Figma: pt 84 (di bawah App Bar 64), pb 110 (termasuk Bottom Nav 72).
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
                    subtitle = "Nama usaha, kategori, dan alamat",
                    onClick = onManageBusinessProfile ?: comingSoon("Kelola Profil Usaha")
                )
            }
            item(key = "projects") {
                ProfileMenuItem(
                    icon = R.drawable.ic_symbol_folder_open,
                    title = "Project Saya",
                    subtitle = "${projects.size} project, $attention perlu perhatian",
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

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun UmkmProfileScreenPreview() {
    PROYEKPAPBFETheme {
        UmkmProfileScreen(
            onNotificationsClick = {},
            onManageBusinessProfile = null,
            onOpenProjects = {},
            onOpenTransactions = {},
            onOpenNotifications = {},
            onSecurity = null,
            onLogout = {},
            onTabSelected = {}
        )
    }
}
