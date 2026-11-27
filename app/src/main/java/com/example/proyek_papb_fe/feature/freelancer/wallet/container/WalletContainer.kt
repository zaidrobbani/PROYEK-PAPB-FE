package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.WalletEntryType
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.freelancer.wallet.viewmodel.WalletVmState
import com.example.proyek_papb_fe.feature.freelancer.wallet.viewmodel.WalletViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletTransactionKind
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletTransactionUi
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val PreviewSettledCount = 3

private fun shortDate(millis: Long): String =
    SimpleDateFormat("d MMM", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis))

private fun WalletVmState.toUiState(showAll: Boolean): WalletUiState {
    val wallet = wallet
    val settled = entries.filter { it.type == WalletEntryType.FUND_RELEASED }.sortedByDescending { it.createdAtMillis ?: 0L }
    val held = entries.filter { it.type == WalletEntryType.FUND_HELD && it.stillHeld }.sortedByDescending { it.createdAtMillis ?: 0L }
    val shownSettled = if (showAll) settled else settled.take(PreviewSettledCount)
    return WalletUiState(
        totalBalance = formatRupiah(wallet?.totalBalance ?: 0),
        availableBalance = formatRupiah(wallet?.availableBalance ?: 0),
        heldBalance = formatRupiah(wallet?.heldBalance ?: 0),
        accountTitle = null,
        accountHolder = null,
        isPrimaryAccount = false,
        transactions = shownSettled.map { tx ->
            WalletTransactionUi(
                tx.id, tx.projectTitle ?: tx.typeLabel, "Pemasukan proyek, ${tx.createdAtMillis?.let(::shortDate).orEmpty()}",
                "+ ${formatRupiah(tx.amount)}", WalletTransactionKind.INCOME
            )
        } + held.map { tx ->
            WalletTransactionUi(
                tx.id, tx.projectTitle ?: tx.typeLabel, "Ditahan sejak ${tx.createdAtMillis?.let(::shortDate).orEmpty()}",
                formatRupiah(tx.amount), WalletTransactionKind.HELD
            )
        },
        hasMoreTransactions = !showAll && settled.size > PreviewSettledCount
    )
}

@Composable
fun WalletContainer(
    onBack: () -> Unit,
    onWithdrawClick: () -> Unit,
    viewModel: WalletViewModel = viewModel(factory = WalletViewModel.factory(AppContainer.paymentRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAllTransactions by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.load() }

    if (uiState.wallet == null) {
        if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        return
    }
    val state = remember(uiState, showAllTransactions) { uiState.toUiState(showAllTransactions) }

    WalletScreen(
        onRefresh = viewModel::refresh,
        state = state,
        onBack = onBack,
        onWithdrawClick = {
            // Keputusan PRD: pencairan dana ke rekening di luar scope MVP (BE belum punya endpoint).
            Toast.makeText(context, "Pencairan dana ke rekening belum tersedia di versi ini.", Toast.LENGTH_LONG).show()
        },
        onChangeAccountClick = {
            Toast.makeText(context, "Pengaturan rekening pencairan belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
        },
        onShowAllTransactionsClick = { showAllTransactions = true }
    )
}
