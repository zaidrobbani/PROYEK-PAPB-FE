package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.PayoutAccountOptionUi
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.QuickAmountUi
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WithdrawScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WithdrawUiState

/** Batas digit input agar nominal tetap muat di Long (maks. 999 miliar). */
private const val MaxAmountDigits = 12

/** Figma 27:182: chip nominal cepat sebelum "Semua saldo". */
private val QuickAmounts = listOf("Rp 500rb" to 500_000L, "Rp 1 jt" to 1_000_000L)

/** Hanya angka, tanpa nol di depan ("0" atau "007" → "" / "7"). */
private fun sanitizeAmount(input: String): String =
    input.filter(Char::isDigit).trimStart('0').take(MaxAmountDigits)

/**
 * Pesan kesalahan nominal. Nominal kosong baru ditandai setelah tombol "Ajukan Pencairan"
 * ditekan; nominal yang melebihi saldo langsung ditandai saat diketik.
 */
private fun amountError(amount: Long?, available: Long, submitAttempted: Boolean): String? = when {
    amount == null -> if (submitAttempted) "Masukkan nominal pencairan." else null
    amount <= 0 -> "Nominal pencairan harus lebih dari Rp 0."
    amount > available -> "Nominal melebihi saldo tersedia (${formatRupiah(available)})."
    else -> null
}

/**
 * Container Pengajuan Pencairan Dana (Figma 27:161), dibuka dari "Cairkan Dana" di
 * Dompet & Rekening. Saldo dan rekening dibaca dari [WalletStore] (sumber yang sama
 * dengan Dompet & Rekening). Sesuai Figma, nominal awal = semua saldo tersedia dan
 * rekening awal = rekening Utama.
 */
@Composable
fun WithdrawContainer(
    onBack: () -> Unit,
    onWithdrawalSubmitted: (withdrawalId: String) -> Unit
) {
    val summary = WalletStore.summary
    val available = summary.availableBalance

    var amountDigits by rememberSaveable { mutableStateOf(sanitizeAmount(available.toString())) }
    var selectedAccountNumber by rememberSaveable { mutableStateOf(summary.primaryAccount?.accountNumber) }
    var submitAttempted by rememberSaveable { mutableStateOf(false) }

    val amount = amountDigits.toLongOrNull()
    val selectedAccount = summary.payoutAccounts.firstOrNull { it.accountNumber == selectedAccountNumber }
    val amountError = amountError(amount, available, submitAttempted)
    val accountError = if (submitAttempted && selectedAccount == null && summary.payoutAccounts.isNotEmpty()) {
        "Pilih rekening tujuan."
    } else null
    val validAmount = amount?.takeIf { it in 1..available } ?: 0L

    val state = WithdrawUiState(
        availableBalance = formatRupiah(available),
        amountDigits = amountDigits,
        quickAmounts = QuickAmounts.map { (label, value) ->
            QuickAmountUi(label, value, isSelected = amount == value && value != available)
        } + QuickAmountUi("Semua saldo", available, isSelected = amount != null && amount == available),
        amountError = amountError,
        accounts = summary.payoutAccounts.map {
            PayoutAccountOptionUi(
                key = it.accountNumber,
                title = it.displayTitle,
                holder = it.displayHolder,
                isSelected = it.accountNumber == selectedAccount?.accountNumber
            )
        },
        accountError = accountError,
        nominal = formatRupiah(validAmount),
        fee = formatRupiah(WalletStore.WithdrawalFee),
        received = formatRupiah((validAmount - WalletStore.WithdrawalFee).coerceAtLeast(0)),
        estimate = WalletStore.WithdrawalEstimate
    )

    WithdrawScreen(
        state = state,
        onBack = onBack,
        onAmountChange = { amountDigits = sanitizeAmount(it) },
        onQuickAmountClick = { amountDigits = sanitizeAmount(it.toString()) },
        onAccountSelect = { selectedAccountNumber = it },
        onSubmitClick = {
            submitAttempted = true
            val account = selectedAccount
            if (amount != null && amountError(amount, available, true) == null && account != null) {
                WalletStore.submitWithdrawal(amount, account)?.let { onWithdrawalSubmitted(it.id) }
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WithdrawContainerPreview() {
    WithdrawContainer(onBack = {}, onWithdrawalSubmitted = {})
}
