package com.example.proyek_papb_fe.feature.freelancer.wallet.component

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Data tampilan "Pencairan sedang diproses" (sudah diformat). */
data class WithdrawResultUiState(
    /** "TMK-WD-20261011-0042". */
    val withdrawalId: String,
    /** "Rp 2.850.000". */
    val amount: String,
    /** "BCA akhiran 4821". */
    val accountTitle: String,
    /** "BCA". */
    val bankName: String,
    /** "1x24 jam kerja". */
    val estimate: String,
    /** "11 Okt 2026, 10.20 WIB". */
    val requestedAt: String,
    /** "Saldo tersedia kini", mis. "Rp 0". */
    val remainingBalance: String
)

/**
 * Figma: Pencairan Sedang Diproses (27:218), tampil setelah "Ajukan Pencairan".
 * Tanpa app bar (Main pt 80, px 17, gap 16, rata tengah); Back sistem dan
 * "Kembali ke Dompet" sama-sama kembali ke Dompet & Rekening.
 */
@Composable
fun WithdrawResultScreen(
    state: WithdrawResultUiState,
    onBackToWalletClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
            .verticalScroll(rememberScrollState())
            .padding(start = 17.dp, end = 17.dp, top = 80.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(84.dp).background(WithdrawColors.StatusIconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_schedule_send), contentDescription = null, tint = WithdrawColors.StatusIcon)
        }
        Text(
            text = "Pencairan sedang diproses",
            style = WithdrawTypography.Headline,
            color = TemuKaryaColors.TextHeading,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .background(WithdrawColors.PendingChip, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_hourglass_top),
                contentDescription = null,
                tint = WithdrawColors.OnPendingChip,
                modifier = Modifier.size(15.dp)
            )
            Text("Diproses", style = WithdrawTypography.Chip, color = WithdrawColors.OnPendingChip)
        }
        Text(
            text = "Dana akan masuk ke rekening ${state.bankName} kamu paling lambat ${state.estimate}. " +
                "Status bisa dipantau di Dompet.",
            style = WithdrawTypography.Body,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TemuKaryaColors.SurfaceCard, WithdrawCardShape)
                .border(1.dp, WithdrawColors.CardBorder, WithdrawCardShape)
                .padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WithdrawDetailRow("ID pencairan", state.withdrawalId)
            WithdrawDetailRow("Nominal", state.amount, valueStyle = WithdrawTypography.RowValueStrong)
            WithdrawDetailRow("Rekening", state.accountTitle)
            WithdrawDetailRow("Diajukan", state.requestedAt)
            WithdrawDetailRow("Saldo tersedia kini", state.remainingBalance)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .clip(WithdrawButtonShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(role = Role.Button, onClick = onBackToWalletClick),
            contentAlignment = Alignment.Center
        ) {
            Text("Kembali ke Dompet", style = WithdrawTypography.Button, color = TemuKaryaColors.OnAction)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(51.dp)
                .clip(WithdrawButtonShape)
                .border(1.5.dp, TemuKaryaColors.ActionPrimary, WithdrawButtonShape)
                .clickable(role = Role.Button, onClick = onHomeClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_nav_home),
                contentDescription = null,
                tint = TemuKaryaColors.ActionPrimary,
                modifier = Modifier.size(19.dp)
            )
            Text("Ke Beranda", style = WithdrawTypography.Button, color = TemuKaryaColors.ActionPrimary)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WithdrawResultScreenPreview() {
    WithdrawResultScreen(
        state = WithdrawResultUiState(
            withdrawalId = "TMK-WD-20261011-0042",
            amount = "Rp 2.850.000",
            accountTitle = "BCA akhiran 4821",
            bankName = "BCA",
            estimate = "1x24 jam kerja",
            requestedAt = "11 Okt 2026, 10.20 WIB",
            remainingBalance = "Rp 0"
        ),
        onBackToWalletClick = {},
        onHomeClick = {}
    )
}
