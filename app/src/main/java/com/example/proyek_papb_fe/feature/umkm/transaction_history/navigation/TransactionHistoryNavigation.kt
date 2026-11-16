package com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.transaction_history.container.TransactionHistoryScreen
import kotlinx.serialization.Serializable

@Serializable
data object TransactionHistoryRoute

fun NavController.navigateToTransactionHistory() {
    navigate(TransactionHistoryRoute) { launchSingleTop = true }
}

fun NavGraphBuilder.transactionHistoryScreen(
    onBack: () -> Unit,
    onDownloadStatement: () -> Unit,
    onAdvancedFilter: () -> Unit,
    onTransactionClick: (code: String) -> Unit
) {
    composable<TransactionHistoryRoute> {
        TransactionHistoryScreen(
            onBack = onBack,
            onDownloadStatement = onDownloadStatement,
            onAdvancedFilter = onAdvancedFilter,
            onTransactionClick = onTransactionClick
        )
    }
}
