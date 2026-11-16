package com.example.proyek_papb_fe.feature.umkm.transaction_history.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransaction
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransactionStore
import com.example.proyek_papb_fe.feature.umkm.data.formatMonthLong
import com.example.proyek_papb_fe.feature.umkm.data.formatMonthShort
import com.example.proyek_papb_fe.feature.umkm.data.transactionMonthKey
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionCard
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionHistoryHeader
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionMonthHeader
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionSearchAndPeriod
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionTab
import com.example.proyek_papb_fe.feature.umkm.transaction_history.component.TransactionTabs

private val PageGutter = 16.903.dp

/**
 * Riwayat Transaksi UMKM (Blueprint TK-F-U-20/22 dan Fungsi 3). Hanya menampilkan
 * DATA DUMMY lokal; nominal tidak dihitung di aplikasi dan tidak ada transaksi sungguhan.
 */
@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit,
    onDownloadStatement: () -> Unit,
    onAdvancedFilter: () -> Unit,
    onTransactionClick: (code: String) -> Unit,
    modifier: Modifier = Modifier,
    transactions: List<UmkmTransaction> = UmkmTransactionStore.transactions
) {
    var tab by rememberSaveable { mutableStateOf(TransactionTab.Semua) }
    var query by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf<String?>(null) }

    val sorted = transactions.sortedByDescending { it.paidAtMillis }
    val counts = TransactionTab.entries.associateWith { t -> sorted.count(t::matches) }
    val months = sorted
        .distinctBy { transactionMonthKey(it.paidAtMillis) }
        .map { transactionMonthKey(it.paidAtMillis) to formatMonthShort(it.paidAtMillis) }
    val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
    val visible = sorted.filter { transaction ->
        val haystack = "${transaction.projectTitle} ${transaction.freelancerName} ${transaction.code}".lowercase()
        tab.matches(transaction) &&
            (month == null || transactionMonthKey(transaction.paidAtMillis) == month) &&
            tokens.all { it in haystack }
    }
    val groups = visible.groupBy { transactionMonthKey(it.paidAtMillis) }
    val latestMonth = months.firstOrNull()?.first

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Riwayat Transaksi", onBack = onBack)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding(),
            contentPadding = PaddingValues(start = PageGutter, end = PageGutter, top = 3.61.dp, bottom = 32.dp)
        ) {
            item(key = "header") {
                TransactionHistoryHeader(
                    onDownload = onDownloadStatement,
                    onAdvancedFilter = onAdvancedFilter,
                    modifier = Modifier.padding(bottom = 8.451.dp)
                )
            }
            item(key = "tabs") {
                TransactionTabs(
                    selected = tab,
                    counts = counts,
                    onSelect = { tab = it },
                    modifier = Modifier.padding(bottom = 8.451.dp)
                )
            }
            item(key = "search") {
                TransactionSearchAndPeriod(
                    query = query,
                    onQueryChange = { query = it },
                    months = months,
                    selectedMonth = month,
                    onMonthSelect = { month = it },
                    modifier = Modifier.padding(bottom = 12.677.dp)
                )
            }
            if (groups.isEmpty()) {
                item(key = "empty") {
                    // Figma tidak punya keadaan kosong; teks sederhana ini tambahan.
                    Text(
                        text = if (tab == TransactionTab.Dibatalkan && query.isBlank()) {
                            "Belum ada transaksi yang dibatalkan."
                        } else {
                            "Tidak ada transaksi yang cocok."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 33.805.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.79.sp,
                            lineHeight = 21.128.sp
                        )
                    )
                }
            }
            groups.forEach { (key, items) ->
                item(key = "month-$key") {
                    TransactionMonthHeader(
                        title = formatMonthLong(items.first().paidAtMillis),
                        count = items.size,
                        isLatest = key == latestMonth
                    )
                }
                items(items, key = { it.code }) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.code) },
                        modifier = Modifier.padding(top = 8.451.dp)
                    )
                }
                item(key = "gap-$key") {
                    Spacer(Modifier.height(16.903.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun TransactionHistoryScreenPreview() {
    PROYEKPAPBFETheme {
        TransactionHistoryScreen(
            onBack = {},
            onDownloadStatement = {},
            onAdvancedFilter = {},
            onTransactionClick = {}
        )
    }
}
