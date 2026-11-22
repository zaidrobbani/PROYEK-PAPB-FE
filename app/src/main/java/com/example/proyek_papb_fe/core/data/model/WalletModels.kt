package com.example.proyek_papb_fe.core.data.model

data class PayoutAccount(
    val bankName: String,
    val accountNumber: String,
    val holderName: String,
    val isPrimary: Boolean
) {
    val suffix: String get() = accountNumber.takeLast(4)
}

enum class WalletTransactionType {
    INCOME,
    WITHDRAWAL,
    HELD
}

data class WalletTransaction(
    val id: String,
    val title: String,
    val type: WalletTransactionType,
    val amount: Long,
    val atMillis: Long,
    val isPending: Boolean = false
)

data class WithdrawalRequest(
    val id: String,
    val amount: Long,
    val fee: Long,
    val account: PayoutAccount,
    val requestedAtMillis: Long,
    val availableBalanceAfter: Long
) {
    val receivedAmount: Long get() = amount - fee
}

data class WalletSummary(
    val availableBalance: Long,
    val payoutAccounts: List<PayoutAccount>,
    val transactions: List<WalletTransaction>
) {
    val heldBalance: Long get() = transactions.filter { it.type == WalletTransactionType.HELD }.sumOf { it.amount }
    val totalBalance: Long get() = availableBalance + heldBalance
    val primaryAccount: PayoutAccount? get() = payoutAccounts.firstOrNull { it.isPrimary } ?: payoutAccounts.firstOrNull()
}
