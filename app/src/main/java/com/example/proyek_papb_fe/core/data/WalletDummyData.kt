package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.PayoutAccount
import com.example.proyek_papb_fe.core.data.model.WalletSummary
import com.example.proyek_papb_fe.core.data.model.WalletTransaction
import com.example.proyek_papb_fe.core.data.model.WalletTransactionType
import java.util.Calendar
import java.util.TimeZone

/**
 * Data Dompet & Rekening freelancer (sementara, sebelum ada backend), sesuai Figma
 * "Dompet Freelancer" (27:80). Satu sumber saldo untuk layar Dompet & Rekening, kartu
 * saldo di Beranda, dan menu di Profil.
 */
object WalletDummyData {

    private fun date(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).apply {
            clear(); set(year, month - 1, day, 10, 0)
        }.timeInMillis

    val summary = WalletSummary(
        availableBalance = 2_850_000,
        payoutAccounts = listOf(
            PayoutAccount(
                bankName = "BCA",
                accountNumber = "0372154821",
                holderName = FreelancerProfileDummyData.current.fullName,
                isPrimary = true
            )
        ),
        transactions = listOf(
            WalletTransaction("tx-6", "Foto Katalog Menu Warung Kopi", WalletTransactionType.INCOME, 950_000, date(2026, 10, 18)),
            WalletTransaction("tx-5", "Website Toko Online Batik", WalletTransactionType.INCOME, 1_900_000, date(2026, 10, 9)),
            WalletTransaction("tx-4", "Pencairan ke BCA", WalletTransactionType.WITHDRAWAL, 500_000, date(2026, 10, 2)),
            WalletTransaction("tx-3", "Pencairan ke BCA", WalletTransactionType.WITHDRAWAL, 1_200_000, date(2026, 9, 20)),
            WalletTransaction("tx-2", "Konten Feed Instagram Kafe", WalletTransactionType.INCOME, 600_000, date(2026, 9, 14)),
            WalletTransaction("tx-1", "Rebranding Pouch Kopi", WalletTransactionType.HELD, 1_500_000, date(2026, 10, 24))
        )
    )
}
