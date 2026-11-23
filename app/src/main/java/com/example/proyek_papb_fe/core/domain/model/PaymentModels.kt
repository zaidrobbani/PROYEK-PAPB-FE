package com.example.proyek_papb_fe.core.domain.model

object PaymentStatusCode {
    const val MENUNGGU = "MENUNGGU"
    const val BERHASIL = "BERHASIL"
    const val GAGAL = "GAGAL"
    const val DIBATALKAN = "DIBATALKAN"
}

object FundStatusCode {
    const val HELD = "HELD"
    const val RELEASED = "RELEASED"
}

data class PaymentAttempt(
    val attemptNumber: Int,
    val status: String,
    val failureReason: String?,
    val attemptedAtMillis: Long?
)

data class PaymentDetail(
    val id: String,
    val projectId: String,
    val projectTitle: String,
    val transactionNumber: String?,
    val status: String,
    val statusLabel: String,
    val fundStatus: String?,
    val displayStatus: String,
    val displayStatusLabel: String,
    val paymentMethodLabel: String,
    val grossAmount: Long,
    val platformFee: Long,
    val freelancerAmount: Long,
    val totalAmount: Long,
    val commissionPercent: Int,
    val expiresAtMillis: Long?,
    val secondsRemaining: Int?,
    val paidAtMillis: Long?,
    val createdAtMillis: Long?,
    val scopeOfWork: String,
    val agreedDeadline: String,
    val durationDays: Int,
    val umkm: UserSummary,
    val freelancer: UserSummary,
    val attempts: List<PaymentAttempt>
) {
    val isPaid: Boolean get() = status == PaymentStatusCode.BERHASIL
}

/** Filter tab Riwayat Transaksi (enum `transaction_groups`). */
enum class TransactionGroup { ALL, PENDING, HELD, COMPLETED, CANCELLED }

data class PaymentHistoryItem(
    val paymentId: String,
    val transactionNumber: String?,
    val projectId: String,
    val projectTitle: String,
    val freelancerName: String,
    val amount: Long,
    val status: String,
    val fundStatus: String?,
    val displayStatus: String,
    val displayStatusLabel: String,
    val occurredAtMillis: Long?
)

data class PaymentHistory(
    val items: List<PaymentHistoryItem>,
    val counts: Map<String, Int>
)

data class Wallet(
    val availableBalance: Long,
    val heldBalance: Long,
    val totalBalance: Long
)

object WalletEntryType {
    const val FUND_HELD = "FUND_HELD"
    const val FUND_RELEASED = "FUND_RELEASED"
}

data class WalletEntry(
    val id: String,
    val type: String,
    val typeLabel: String,
    val amount: Long,
    val projectId: String?,
    val projectTitle: String?,
    val stillHeld: Boolean,
    val createdAtMillis: Long?
)
