package com.example.proyek_papb_fe.core.data.model

/** Rekening tujuan pencairan dana freelancer. */
data class PayoutAccount(
    val bankName: String,
    val accountNumber: String,
    val holderName: String,
    /** true = rekening "Utama" yang dipakai untuk pencairan. */
    val isPrimary: Boolean
) {
    /** 4 digit terakhir nomor rekening, mis. "4821". */
    val suffix: String get() = accountNumber.takeLast(4)
}

enum class WalletTransactionType {
    /** Pembayaran project yang sudah masuk ke saldo tersedia. */
    INCOME,
    /** Pencairan saldo tersedia ke rekening. */
    WITHDRAWAL,
    /** Dana project berjalan yang masih ditahan (escrow) sampai UMKM menyetujui hasil. */
    HELD
}

data class WalletTransaction(
    val id: String,
    val title: String,
    val type: WalletTransactionType,
    /** Nominal positif; arah (masuk/keluar) ditentukan [type]. */
    val amount: Long,
    /** Tanggal transaksi; untuk [WalletTransactionType.HELD] = mulai ditahan. */
    val atMillis: Long,
    /** Khusus [WalletTransactionType.WITHDRAWAL]: true = pengajuan pencairan masih diproses. */
    val isPending: Boolean = false
)

/**
 * Pengajuan Pencairan Dana yang sudah dikirim (Figma 27:218 "Pencairan sedang diproses").
 * Lokal saja: belum ada transfer bank sungguhan.
 */
data class WithdrawalRequest(
    /** "TMK-WD-20261011-0042"; sama dengan id transaksi di riwayat Dompet. */
    val id: String,
    val amount: Long,
    /** Biaya transfer (Figma: Rp 0). */
    val fee: Long,
    val account: PayoutAccount,
    val requestedAtMillis: Long,
    /** Saldo tersedia setelah pengajuan ("Saldo tersedia kini"). */
    val availableBalanceAfter: Long
) {
    /** "Diterima di rekening". */
    val receivedAmount: Long get() = amount - fee
}

/**
 * Ringkasan Dompet & Rekening freelancer. Saldo tertahan dihitung dari transaksi
 * [WalletTransactionType.HELD] agar kartu saldo dan riwayat tidak pernah berbeda.
 */
data class WalletSummary(
    val availableBalance: Long,
    val payoutAccounts: List<PayoutAccount>,
    val transactions: List<WalletTransaction>
) {
    val heldBalance: Long get() = transactions.filter { it.type == WalletTransactionType.HELD }.sumOf { it.amount }
    val totalBalance: Long get() = availableBalance + heldBalance
    val primaryAccount: PayoutAccount? get() = payoutAccounts.firstOrNull { it.isPrimary } ?: payoutAccounts.firstOrNull()
}
