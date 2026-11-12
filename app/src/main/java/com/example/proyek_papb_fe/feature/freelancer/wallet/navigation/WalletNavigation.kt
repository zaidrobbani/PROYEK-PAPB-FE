package com.example.proyek_papb_fe.feature.freelancer.wallet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.wallet.container.WalletContainer
import com.example.proyek_papb_fe.feature.freelancer.wallet.container.WithdrawContainer
import com.example.proyek_papb_fe.feature.freelancer.wallet.container.WithdrawResultContainer
import kotlinx.serialization.Serializable

/** Figma: Dompet Freelancer (27:80), judul "Dompet & Rekening". Halaman detail (tanpa bottom bar). */
@Serializable
data object WalletRoute

/** Figma: Ajukan Pencairan Dana (27:161), Pengajuan Pencairan Dana dari "Cairkan Dana". */
@Serializable
data object WithdrawRoute

/** Figma: Pencairan Sedang Diproses (27:218), setelah "Ajukan Pencairan". */
@Serializable
data class WithdrawResultRoute(val withdrawalId: String)

fun NavController.navigateToWallet() {
    navigate(WalletRoute) {
        launchSingleTop = true
    }
}

fun NavController.navigateToWithdraw() {
    navigate(WithdrawRoute) {
        launchSingleTop = true
    }
}

fun NavController.navigateToWithdrawResult(
    withdrawalId: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(WithdrawResultRoute(withdrawalId), builder)
}

fun NavGraphBuilder.walletScreen(
    onBack: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    composable<WalletRoute> {
        WalletContainer(onBack = onBack, onWithdrawClick = onWithdrawClick)
    }
}

fun NavGraphBuilder.withdrawScreen(
    onBack: () -> Unit,
    onWithdrawalSubmitted: (withdrawalId: String) -> Unit
) {
    composable<WithdrawRoute> {
        WithdrawContainer(onBack = onBack, onWithdrawalSubmitted = onWithdrawalSubmitted)
    }
}

fun NavGraphBuilder.withdrawResultScreen(
    onBackToWallet: () -> Unit,
    onHomeClick: () -> Unit
) {
    composable<WithdrawResultRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<WithdrawResultRoute>()
        WithdrawResultContainer(
            withdrawalId = route.withdrawalId,
            onBackToWallet = onBackToWallet,
            onHomeClick = onHomeClick
        )
    }
}
