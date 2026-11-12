package com.example.proyek_papb_fe.feature.freelancer.wallet.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Jenis baris riwayat: menentukan ikon, warna, dan tanda nominal. */
enum class WalletTransactionKind { INCOME, WITHDRAWAL, HELD }

/** Satu baris Riwayat Transaksi (sudah diformat). */
data class WalletTransactionUi(
    val id: String,
    val title: String,
    /** "Pemasukan project, 18 Okt" / "Berhasil, 2 Okt" / "Ditahan sejak 24 Okt". */
    val subtitle: String,
    /** "+ Rp 950.000" / "− Rp 500.000" / "Rp 1.500.000". */
    val amountLabel: String,
    val kind: WalletTransactionKind
)

/** Data tampilan Dompet & Rekening (sudah diformat). */
data class WalletUiState(
    val totalBalance: String,
    val availableBalance: String,
    val heldBalance: String,
    /** "BCA akhiran 4821"; null = belum ada rekening pencairan. */
    val accountTitle: String?,
    /** "a.n. Andi Ramadhan". */
    val accountHolder: String?,
    val isPrimaryAccount: Boolean,
    val transactions: List<WalletTransactionUi>,
    /** true = masih ada transaksi yang belum ditampilkan (link "Semua" aktif). */
    val hasMoreTransactions: Boolean
)

private object WalletTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp
    )

    val TotalLabel = style(FontWeight.Medium, 12.7f)
    val Total = style(FontWeight.Bold, 29.6f)
    val TileLabel = style(FontWeight.Medium, 11.6f)
    val TileValue = style(FontWeight.Bold, 16.9f)
    val TileCaption = style(FontWeight.Normal, 10.6f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val Info = style(FontWeight.Normal, 12.7f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val Link = style(FontWeight.SemiBold, 12.7f)
    val AccountTitle = style(FontWeight.SemiBold, 14.8f)
    val Caption = style(FontWeight.Normal, 11.6f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val TxTitle = style(FontWeight.SemiBold, 13.7f)
    val TxAmount = style(FontWeight.Bold, 13.7f)
}

private object WalletColors {
    val Muted = Color(0xFF6B5A50)
    val Tile = Color(0x8CFFFFFF) // rgba(255,255,255,0.55)
    val Info = Color(0x99DBE3C4) // rgba(219,227,196,0.6)
    val OnInfo = Color(0xFF424A33)
    val Badge = Color(0xFFEDE0C2)
    val CardBorder = Color(0xFFE2D5BA)
    val PrimaryChip = Color(0xFFC9D1B5)
    val OnPrimaryChip = Color(0xFF364323)
    val Outgoing = Color(0xFFBA1A1A)
}

private val CardShape = RoundedCornerShape(16.dp)

/**
 * Figma: Dompet Freelancer (27:80), judul "Dompet & Rekening".
 * App bar Detail tetap di atas; isi di-scroll (pt 20, px 17, pb 32, jarak antar bagian 16).
 */
@Composable
fun WalletScreen(
    state: WalletUiState,
    onBack: () -> Unit,
    onWithdrawClick: () -> Unit,
    onChangeAccountClick: () -> Unit,
    onShowAllTransactionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Dompet & Rekening", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BalanceCard(state = state, onWithdrawClick = onWithdrawClick)
            HeldBalanceInfo()
            SectionHeader(title = "Rekening Pencairan", actionLabel = "Ubah", onActionClick = onChangeAccountClick)
            if (state.accountTitle != null) {
                PayoutAccountCard(title = state.accountTitle, holder = state.accountHolder.orEmpty(), isPrimary = state.isPrimaryAccount)
            }
            SectionHeader(
                title = "Riwayat Transaksi",
                actionLabel = if (state.hasMoreTransactions) "Semua" else null,
                onActionClick = onShowAllTransactionsClick
            )
            TransactionList(transactions = state.transactions)
        }
    }
}

@Composable
private fun BalanceCard(state: WalletUiState, onWithdrawClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceSand, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Total Saldo", style = WalletTypography.TotalLabel, color = WalletColors.Muted)
        Text(state.totalBalance, style = WalletTypography.Total, color = TemuKaryaColors.TextPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BalanceTile(R.drawable.ic_wallet_available, "Saldo Tersedia", state.availableBalance, "Dapat dicairkan", Modifier.weight(1f))
            BalanceTile(R.drawable.ic_wallet_lock, "Saldo Tertahan", state.heldBalance, "Dari project yang berjalan", Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TemuKaryaColors.SurfaceWhite)
                .clickable(role = Role.Button, onClick = onWithdrawClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_wallet_payments), contentDescription = null, tint = TemuKaryaColors.ActionPrimary)
            Text("Cairkan Dana", style = WalletTypography.Button, color = TemuKaryaColors.ActionPrimary)
        }
    }
}

@Composable
private fun BalanceTile(@DrawableRes iconRes: Int, label: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(WalletColors.Tile, CardShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(iconRes), contentDescription = null, tint = TemuKaryaColors.TextPrimary)
            Text(label, style = WalletTypography.TileLabel, color = WalletColors.Muted, maxLines = 1)
        }
        Text(value, style = WalletTypography.TileValue, color = TemuKaryaColors.TextPrimary, maxLines = 1)
        Text(caption, style = WalletTypography.TileCaption, color = WalletColors.Muted, maxLines = 1)
    }
}

@Composable
private fun HeldBalanceInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WalletColors.Info, CardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(R.drawable.ic_wallet_info), contentDescription = null, tint = TemuKaryaColors.Olive)
        Text(
            text = "Saldo tertahan baru masuk ke saldo tersedia setelah UMKM menyetujui hasil project.",
            style = WalletTypography.Info,
            color = WalletColors.OnInfo,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String?, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = WalletTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = WalletTypography.Link,
                color = TemuKaryaColors.ActionPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(role = Role.Button, onClick = onActionClick)
            )
        }
    }
}

@Composable
private fun PayoutAccountCard(title: String, holder: String, isPrimary: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, CardShape)
            .border(1.dp, WalletColors.CardBorder, CardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(R.drawable.ic_wallet_bank, TemuKaryaColors.ActionPrimary, size = 40)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = WalletTypography.AccountTitle, color = TemuKaryaColors.TextPrimary, maxLines = 1)
            Text(holder, style = WalletTypography.Caption, color = TemuKaryaColors.TextBody, maxLines = 1)
        }
        if (isPrimary) {
            Text(
                text = "Utama",
                style = WalletTypography.Chip,
                color = WalletColors.OnPrimaryChip,
                modifier = Modifier
                    .background(WalletColors.PrimaryChip, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TransactionList(transactions: List<WalletTransactionUi>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, CardShape)
            .padding(6.dp)
    ) {
        if (transactions.isEmpty()) {
            Text(
                text = "Belum ada transaksi.",
                style = WalletTypography.Caption,
                color = TemuKaryaColors.TextBody,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }
        transactions.forEachIndexed { index, tx ->
            if (index > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(WalletColors.CardBorder))
            }
            TransactionRow(tx)
        }
    }
}

@Composable
private fun TransactionRow(tx: WalletTransactionUi) {
    val (iconRes, color) = when (tx.kind) {
        WalletTransactionKind.INCOME -> R.drawable.ic_wallet_income to TemuKaryaColors.Olive
        WalletTransactionKind.WITHDRAWAL -> R.drawable.ic_wallet_outgoing to WalletColors.Outgoing
        WalletTransactionKind.HELD -> R.drawable.ic_wallet_lock to TemuKaryaColors.TextPlaceholder
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(iconRes, color, size = 36)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(tx.title, style = WalletTypography.TxTitle, color = TemuKaryaColors.TextPrimary)
            Text(tx.subtitle, style = WalletTypography.Caption, color = TemuKaryaColors.TextBody)
        }
        Text(tx.amountLabel, style = WalletTypography.TxAmount, color = color, maxLines = 1)
    }
}

@Composable
private fun IconBadge(@DrawableRes iconRes: Int, tint: Color, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(WalletColors.Badge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(iconRes), contentDescription = null, tint = tint)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WalletScreenPreview() {
    WalletScreen(
        state = WalletUiState(
            totalBalance = "Rp 4.350.000",
            availableBalance = "Rp 2.850.000",
            heldBalance = "Rp 1.500.000",
            accountTitle = "BCA akhiran 4821",
            accountHolder = "a.n. Andi Ramadhan",
            isPrimaryAccount = true,
            transactions = listOf(
                WalletTransactionUi("1", "Foto Katalog Menu Warung Kopi", "Pemasukan project, 18 Okt", "+ Rp 950.000", WalletTransactionKind.INCOME),
                WalletTransactionUi("2", "Pencairan ke BCA", "Berhasil, 2 Okt", "− Rp 500.000", WalletTransactionKind.WITHDRAWAL),
                WalletTransactionUi("3", "Rebranding Pouch Kopi", "Ditahan sejak 24 Okt", "Rp 1.500.000", WalletTransactionKind.HELD)
            ),
            hasMoreTransactions = true
        ),
        onBack = {}, onWithdrawClick = {}, onChangeAccountClick = {}, onShowAllTransactionsClick = {}
    )
}
