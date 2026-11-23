package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.core.data.model.WithdrawalRequest
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WithdrawResultScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WithdrawResultUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun requestedAtLabel(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis)) + " WIB"

private fun WithdrawalRequest.toUiState() = WithdrawResultUiState(
    withdrawalId = id,
    amount = formatRupiah(amount),
    accountTitle = account.displayTitle,
    bankName = account.bankName,
    estimate = WalletStore.WithdrawalEstimate,
    requestedAt = requestedAtLabel(requestedAtMillis),
    remainingBalance = formatRupiah(availableBalanceAfter)
)

@Composable
fun WithdrawResultContainer(
    withdrawalId: String,
    onBackToWallet: () -> Unit,
    onHomeClick: () -> Unit
) {
    val request = WalletStore.withdrawal(withdrawalId)
    if (request == null) {
        LaunchedEffect(withdrawalId) { onBackToWallet() }
        return
    }
    WithdrawResultScreen(
        state = request.toUiState(),
        onBackToWalletClick = onBackToWallet,
        onHomeClick = onHomeClick
    )
}
