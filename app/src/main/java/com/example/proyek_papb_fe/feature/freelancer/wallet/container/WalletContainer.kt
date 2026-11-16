package com.example.proyek_papb_fe.feature.freelancer.wallet.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.core.data.model.WalletSummary
import com.example.proyek_papb_fe.core.data.model.WalletTransactionType
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletTransactionKind
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletTransactionUi
import com.example.proyek_papb_fe.feature.freelancer.wallet.component.WalletUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Figma: 3 transaksi selesai terbaru + saldo yang masih ditahan, sebelum "Semua". */
private const val PreviewSettledCount = 3

/** "18 Okt" (WIB). */
private fun shortDate(millis: Long): String =
    SimpleDateFormat("d MMM", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis))

/**
 * Riwayat: transaksi selesai (pemasukan & pencairan) terbaru dulu, lalu dana yang masih
 * ditahan selalu tampil di akhir. Tanpa [showAll] hanya 3 transaksi selesai terbaru.
 */
private fun WalletSummary.toUiState(showAll: Boolean): WalletUiState {
    val settled = transactions.filter { it.type != WalletTransactionType.HELD }.sortedByDescending { it.atMillis }
    val held = transactions.filter { it.type == WalletTransactionType.HELD }.sortedByDescending { it.atMillis }
    val shownSettled = if (showAll) settled else settled.take(PreviewSettledCount)
    val account = primaryAccount
    return WalletUiState(
        totalBalance = formatRupiah(totalBalance),
        availableBalance = formatRupiah(availableBalance),
        heldBalance = formatRupiah(heldBalance),
        accountTitle = account?.displayTitle,
        accountHolder = account?.displayHolder,
        isPrimaryAccount = account?.isPrimary == true,
        transactions = (shownSettled + held).map { tx ->
            when (tx.type) {
                WalletTransactionType.INCOME -> WalletTransactionUi(
                    tx.id, tx.title, "Pemasukan proyek, ${shortDate(tx.atMillis)}", "+ ${formatRupiah(tx.amount)}", WalletTransactionKind.INCOME
                )
                WalletTransactionType.WITHDRAWAL -> WalletTransactionUi(
                    tx.id, tx.title, "${if (tx.isPending) "Diproses" else "Berhasil"}, ${shortDate(tx.atMillis)}", "− ${formatRupiah(tx.amount)}", WalletTransactionKind.WITHDRAWAL
                )
                WalletTransactionType.HELD -> WalletTransactionUi(
                    tx.id, tx.title, "Ditahan sejak ${shortDate(tx.atMillis)}", formatRupiah(tx.amount), WalletTransactionKind.HELD
                )
            }
        },
        hasMoreTransactions = !showAll && settled.size > PreviewSettledCount
    )
}

/**
 * Container Dompet & Rekening (Figma 27:80), dibuka dari Profil (dan kartu saldo Beranda).
 * Data dari [WalletStore] (lokal). "Semua" menampilkan seluruh riwayat di layar ini.
 * "Cairkan Dana" membuka Pengajuan Pencairan Dana (Figma 27:161). Ubah rekening belum
 * punya desain/alur, jadi sementara hanya memberi tahu bahwa fiturnya segera hadir.
 */
@Composable
fun WalletContainer(onBack: () -> Unit, onWithdrawClick: () -> Unit) {
    val context = LocalContext.current
    var showAllTransactions by rememberSaveable { mutableStateOf(false) }
    val summary = WalletStore.summary
    val state = remember(summary, showAllTransactions) { summary.toUiState(showAllTransactions) }
    val comingSoon = { feature: String -> Toast.makeText(context, "$feature segera hadir", Toast.LENGTH_SHORT).show() }

    WalletScreen(
        state = state,
        onBack = onBack,
        onWithdrawClick = onWithdrawClick,
        onChangeAccountClick = { comingSoon("Ubah rekening pencairan") },
        onShowAllTransactionsClick = { showAllTransactions = true }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WalletContainerPreview() {
    WalletContainer(onBack = {}, onWithdrawClick = {})
}
