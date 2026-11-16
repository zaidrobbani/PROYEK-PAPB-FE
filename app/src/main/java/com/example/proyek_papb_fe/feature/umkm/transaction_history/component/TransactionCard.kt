package com.example.proyek_papb_fe.feature.umkm.transaction_history.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.Primary40
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.FundStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransaction
import com.example.proyek_papb_fe.feature.umkm.data.formatTransactionTime
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight
    )

/** Judul kelompok bulan, misalnya "Oktober 2026 •  3 Transaksi". */
@Composable
fun TransactionMonthHeader(
    title: String,
    count: Int,
    isLatest: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.226.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = colors.onTertiaryContainer,
                style = text(FontWeight.SemiBold, 14.79.sp, 21.128.sp)
            )
            // Figma: titik hijau untuk bulan terbaru, krem untuk bulan sebelumnya.
            Box(
                modifier = Modifier
                    .size(8.451.dp)
                    .background(if (isLatest) colors.secondary else colors.outlineVariant, CircleShape)
            )
        }
        Text(
            text = "$count Transaksi",
            color = colors.outline,
            style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
        )
    }
}

@Composable
fun TransactionCard(
    transaction: UmkmTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    val released = transaction.fundStatus == FundStatus.Dilepas

    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            val iconShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .size(46.482.dp)
                    .authDropShadow(iconShape, AuthShadowStyle.Subtle)
                    .background(if (released) SageBadge else colors.surfaceVariant, iconShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(transaction.iconRes), contentDescription = null)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.451.dp, end = 8.451.dp),
                verticalArrangement = Arrangement.spacedBy(2.113.dp)
            ) {
                Text(
                    text = transaction.projectTitle,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text(FontWeight.SemiBold, 14.79.sp, 20.336.sp)
                )
                Text(
                    text = "${transaction.freelancerName}, ${formatTransactionTime(transaction.paidAtMillis)}",
                    color = colors.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
                )
                Text(
                    text = "ID: ${transaction.code}",
                    modifier = Modifier.padding(top = 2.113.dp),
                    color = colors.outlineVariant,
                    style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.621.sp,
                        lineHeight = 14.79.sp,
                        letterSpacing = 0.581.sp
                    )
                )
            }
            Image(painter = painterResource(R.drawable.ic_transaction_chevron), contentDescription = null)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    // Label mengikuti Figma; lihat laporan tentang kecocokannya dengan Blueprint.
                    text = if (released) "Sudah Ditransfer ke Mitra" else "Nominal",
                    color = colors.outline,
                    style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                )
                Text(
                    text = formatRupiah(transaction.amount),
                    color = if (released) colors.onSurface else colors.onTertiaryContainer,
                    style = text(FontWeight.Bold, 16.903.sp, 23.241.sp)
                )
            }
            transaction.fundStatus?.let { FundStatusChip(it) }
        }
    }
}

@Composable
private fun FundStatusChip(status: FundStatus) {
    val colors = MaterialTheme.colorScheme
    val released = status == FundStatus.Dilepas
    Row(
        modifier = Modifier
            .background(if (released) colors.secondary else colors.surfaceVariant, CircleShape)
            .padding(horizontal = 12.677.dp, vertical = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (released) {
            Image(painter = painterResource(R.drawable.ic_transaction_released), contentDescription = null)
        } else {
            Box(
                modifier = Modifier
                    .size(6.338.dp)
                    .background(Primary40, CircleShape)
            )
        }
        Text(
            text = status.label,
            color = if (released) colors.onSecondary else colors.onTertiaryContainer,
            style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
        )
    }
}
