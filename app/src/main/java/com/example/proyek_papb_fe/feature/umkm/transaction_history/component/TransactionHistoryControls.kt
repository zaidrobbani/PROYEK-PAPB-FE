package com.example.proyek_papb_fe.feature.umkm.transaction_history.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.FundStatus
import com.example.proyek_papb_fe.feature.umkm.data.PaymentStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransaction

/** Tab di Figma. "Dibatalkan / Refund" memakai status_pembayaran DIBATALKAN dari Blueprint. */
enum class TransactionTab(val label: String, val showCount: Boolean) {
    Semua("Semua", showCount = true),
    DanaDitahan("Dana Ditahan", showCount = true),
    Selesai("Selesai", showCount = false),
    Dibatalkan("Dibatalkan / Refund", showCount = false);

    fun matches(transaction: UmkmTransaction): Boolean = when (this) {
        Semua -> true
        DanaDitahan -> transaction.fundStatus == FundStatus.Tertahan
        Selesai -> transaction.fundStatus == FundStatus.Dilepas
        Dibatalkan -> transaction.paymentStatus == PaymentStatus.Dibatalkan
    }
}

/** Judul halaman "Riwayat Transaksi" dengan tombol unduh dan filter lanjutan. */
@Composable
fun TransactionHistoryHeader(
    onDownload: () -> Unit,
    onAdvancedFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.451.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_transaction_wallet), contentDescription = null)
            Text(
                text = "Riwayat Transaksi",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.titleLarge.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.015.sp,
                    lineHeight = 25.354.sp,
                    letterSpacing = (-0.4754).sp
                )
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp)) {
            HeaderIconButton(R.drawable.ic_transaction_download, "Unduh rekening koran", onDownload)
            HeaderIconButton(R.drawable.ic_dashboard_filter, "Filter lanjutan", onAdvancedFilter)
        }
    }
}

@Composable
private fun HeaderIconButton(@DrawableRes iconRes: Int, description: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .size(42.256.dp)
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
    }
}

@Composable
fun TransactionTabs(
    selected: TransactionTab,
    counts: Map<TransactionTab, Int>,
    onSelect: (TransactionTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(46.482.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(TransactionTab.entries) { tab ->
            val isSelected = tab == selected
            Row(
                modifier = Modifier
                    .then(if (isSelected) Modifier.authDropShadow(CircleShape, AuthShadowStyle.Subtle) else Modifier)
                    .clip(CircleShape)
                    .background(if (isSelected) colors.primary else colors.tertiaryContainer)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(tab) })
                    .padding(horizontal = 16.903.dp, vertical = 8.451.dp),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tab.label,
                    color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.677.sp,
                        lineHeight = 16.903.sp
                    )
                )
                if (tab.showCount) {
                    Box(
                        modifier = Modifier
                            .size(21.128.dp)
                            .background(
                                if (isSelected) colors.onPrimary.copy(alpha = 0.2f) else SageBadge,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (counts[tab] ?: 0).toString(),
                            color = if (isSelected) colors.onPrimary else OnSageBadge,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.621.sp,
                                lineHeight = 14.79.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kolom cari (7/12 lebar) dan tombol periode (5/12 lebar) seperti grid Figma.
 * [months] berisi pasangan kunci bulan dan label pendek, misalnya "2026-10" ke "Okt 2026".
 */
@Composable
fun TransactionSearchAndPeriod(
    query: String,
    onQueryChange: (String) -> Unit,
    months: List<Pair<String, String>>,
    selectedMonth: String?,
    onMonthSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val textStyle = MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = FontWeight.Normal,
        fontSize = 12.677.sp,
        lineHeight = 16.903.sp
    )
    val labelStyle = MaterialTheme.typography.labelSmall.withFrameMetrics(
        fontWeight = FontWeight.Medium,
        fontSize = 11.621.sp,
        lineHeight = 14.79.sp
    )
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(7f)
                .semantics { contentDescription = "Cari transaksi" },
            singleLine = true,
            textStyle = textStyle.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.482.dp)
                        .registerInputShadow(shape)
                        .background(colors.tertiaryContainer, shape)
                        .padding(start = 12.677.dp, end = 12.677.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.092.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_transaction_search), contentDescription = null)
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Cari transaksi atau proyek...",
                                color = colors.outline,
                                maxLines = 1,
                                style = textStyle
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
        Box(modifier = Modifier.weight(5f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.482.dp)
                    .authDropShadow(shape, AuthShadowStyle.Subtle)
                    .clip(shape)
                    .background(colors.tertiaryContainer)
                    .clickable(role = Role.DropdownList) { expanded = true }
                    .semantics {
                        contentDescription = "Periode: " +
                            (months.firstOrNull { it.first == selectedMonth }?.second ?: "Semua Periode")
                    }
                    .padding(horizontal = 12.677.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_transaction_month), contentDescription = null)
                    Text(
                        // Figma hanya menampilkan satu bulan; pilihan "Semua Periode" tambahan.
                        text = months.firstOrNull { it.first == selectedMonth }?.second ?: "Semua Periode",
                        color = colors.onSurface,
                        maxLines = 1,
                        style = labelStyle
                    )
                }
                Image(painter = painterResource(R.drawable.ic_transaction_dropdown), contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = colors.surfaceContainerLowest
            ) {
                (listOf<Pair<String?, String>>(null to "Semua Periode") + months).forEach { (key, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                color = colors.onSurface,
                                style = labelStyle.copy(
                                    fontWeight = if (key == selectedMonth) FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        },
                        onClick = {
                            onMonthSelect(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
