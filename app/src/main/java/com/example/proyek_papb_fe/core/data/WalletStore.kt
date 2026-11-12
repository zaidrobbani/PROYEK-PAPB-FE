package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.model.PayoutAccount
import com.example.proyek_papb_fe.core.data.model.WalletSummary
import com.example.proyek_papb_fe.core.data.model.WalletTransaction
import com.example.proyek_papb_fe.core.data.model.WalletTransactionType
import com.example.proyek_papb_fe.core.data.model.WithdrawalRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Dompet & Rekening di memori (sementara, sebelum ada backend), diawali dari
 * [WalletDummyData]. Satu sumber saldo untuk Dompet & Rekening, Beranda, Profil, dan
 * Pengajuan Pencairan Dana. Berbasis snapshot state: layar yang membaca [summary]
 * otomatis recompose setelah pencairan diajukan.
 *
 * Pengajuan hanya dicatat lokal (status "Diproses"); tidak ada transfer bank sungguhan.
 * Saat backend tersedia, [submitWithdrawal] diganti panggilan API.
 */
object WalletStore {

    /** Figma 27:161 "Biaya transfer": Rp 0. */
    const val WithdrawalFee: Long = 0

    /** Figma 27:161 / 27:218: dana masuk paling lambat 1x24 jam kerja. */
    const val WithdrawalEstimate: String = "1x24 jam kerja"

    var summary: WalletSummary by mutableStateOf(WalletDummyData.summary)
        private set

    private val withdrawals = mutableStateMapOf<String, WithdrawalRequest>()

    /** Nomor urut ID pencairan; Figma memakai contoh "...-0042". */
    private var nextSequence = 42

    fun withdrawal(id: String): WithdrawalRequest? = withdrawals[id]

    /**
     * Dana project dilepas dari escrow ke dompet freelancer setelah UMKM menyetujui hasil
     * (Figma 17:6338 "Dana sudah diteruskan ke dompet ..."): saldo tersedia bertambah [netAmount]
     * dan riwayat mendapat transaksi "Pemasukan project". Idempoten per project.
     */
    fun releaseProjectFunds(projectId: Int, projectTitle: String, netAmount: Long, atMillis: Long = System.currentTimeMillis()) {
        val id = "tx-release-$projectId"
        val current = summary
        if (netAmount <= 0 || current.transactions.any { it.id == id }) return
        summary = current.copy(
            availableBalance = current.availableBalance + netAmount,
            transactions = current.transactions + WalletTransaction(id, projectTitle, WalletTransactionType.INCOME, netAmount, atMillis)
        )
    }

    /**
     * Ajukan pencairan [amount] ke [account]: saldo tersedia langsung berkurang dan
     * transaksi "Pencairan ke <bank>" berstatus diproses ditambahkan ke riwayat.
     * Mengembalikan null bila nominal/rekening tidak valid (validasi utama ada di form).
     */
    fun submitWithdrawal(
        amount: Long,
        account: PayoutAccount,
        nowMillis: Long = System.currentTimeMillis()
    ): WithdrawalRequest? {
        val current = summary
        if (amount <= 0 || amount > current.availableBalance) return null
        if (current.payoutAccounts.none { it.accountNumber == account.accountNumber }) return null

        val id = "TMK-WD-${idDate(nowMillis)}-${nextSequence++.toString().padStart(4, '0')}"
        val remaining = current.availableBalance - amount
        val request = WithdrawalRequest(
            id = id,
            amount = amount,
            fee = WithdrawalFee,
            account = account,
            requestedAtMillis = nowMillis,
            availableBalanceAfter = remaining
        )
        summary = current.copy(
            availableBalance = remaining,
            transactions = current.transactions + WalletTransaction(
                id = id,
                title = "Pencairan ke ${account.bankName}",
                type = WalletTransactionType.WITHDRAWAL,
                amount = amount,
                atMillis = nowMillis,
                isPending = true
            )
        )
        withdrawals[id] = request
        return request
    }

    private fun idDate(millis: Long): String =
        SimpleDateFormat("yyyyMMdd", Locale.ROOT).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }.format(Date(millis))
}
