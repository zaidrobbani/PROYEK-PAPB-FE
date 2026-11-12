package com.example.proyek_papb_fe.feature.freelancer.dashboard.container

import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardProject
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardScreen

@Composable
fun DashboardContainer(
    onWalletClick: () -> Unit = {},
    onProjectClick: (Int) -> Unit = {}
) {
    val activeProject = DashboardProject(
        id = 1,
        title = "Desain Rebranding Logo & Kemasan Pouch Kopi",
        category = "Desain Grafis",
        budget = "Rp 1.500.000",
        status = "Sedang Dikerjakan",
        progress = 20,
        owner = "Kedai Kopi Senja (Rina Pratiwi, Bandung)",
        daysRemaining = 7
    )

    val recommendations = listOf(
        DashboardProject(
            id = 2,
            title = "Rebranding Kemasan Keripik Singkong Renyah",
            category = "Desain Grafis",
            budget = "Rp 1.200.000",
            status = "Terbuka",
            matchPercentage = 96,
            imageEmoji = "📦",
            imageRes = R.drawable.img_reco_keripik
        ),
        DashboardProject(
            id = 3,
            title = "Desain Menu dan Branding Cafe",
            category = "Desain Grafis",
            budget = "Rp 1.000.000",
            status = "Terbuka",
            matchPercentage = 92,
            imageEmoji = "☕",
            imageRes = R.drawable.img_reco_cafe
        )
    )

    DashboardScreen(
        userName = "Andi",
        availableBalance = "Rp " + WalletStore.summary.availableBalance.toString()
            .reversed().chunked(3).joinToString(".").reversed(),
        activeProject = activeProject,
        recommendations = recommendations,
        onWalletClick = onWalletClick,
        onActiveProjectClick = onProjectClick,
        onProjectClick = onProjectClick
    )
}
