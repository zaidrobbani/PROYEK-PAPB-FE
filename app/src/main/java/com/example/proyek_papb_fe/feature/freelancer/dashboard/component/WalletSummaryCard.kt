package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R

/** Figma: Section - Escrow Wallet Summary Card (17:3718). */
@Composable
internal fun WalletSummaryCard(
    balance: String,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DashboardColors.SurfaceSand)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.451.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_wallet_shield),
                contentDescription = null
            )
            Text(
                text = "Dompet",
                style = DashboardTypography.WalletTitle,
                color = DashboardColors.TextWallet
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.226.dp)) {
            Text(
                text = "Saldo tersedia",
                style = DashboardTypography.LabelSemiBold,
                color = DashboardColors.TextWallet
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.426.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = balance,
                    style = DashboardTypography.Balance,
                    color = DashboardColors.TextWallet,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .weight(1f, fill = false)
                )
                WithdrawButton(
                    onClick = onWithdrawClick,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }
    }
}

@Composable
private fun WithdrawButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 1.056.dp,
                    color = Color.Black.copy(alpha = 0.05f),
                    offset = DpOffset(0.dp, 1.056.dp)
                )
            )
            .clip(shape)
            .background(DashboardColors.Olive)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.79.dp, vertical = 8.451.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cairkan Dana",
            style = DashboardTypography.LabelSemiBold,
            color = Color.White
        )
    }
}
