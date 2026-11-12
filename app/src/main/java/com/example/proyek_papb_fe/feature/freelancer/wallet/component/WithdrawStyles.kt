package com.example.proyek_papb_fe.feature.freelancer.wallet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Tipografi Pengajuan Pencairan Dana (27:161) & Pencairan sedang diproses (27:218); line-height 1.4. */
internal object WithdrawTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(
        fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp
    )

    val BalanceLabel = style(FontWeight.Medium, 12.7f)
    val Headline = style(FontWeight.Bold, 23.2f)
    val FieldLabel = style(FontWeight.SemiBold, 12.7f)
    val InputPrefix = style(FontWeight.Bold, 16.9f)
    val InputValue = style(FontWeight.Bold, 21.1f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val SectionTitle = style(FontWeight.Bold, 15.8f)
    val CardTitle = style(FontWeight.Bold, 14.8f)
    val AccountTitle = style(FontWeight.SemiBold, 14.8f)
    val Caption = style(FontWeight.Normal, 11.6f)
    val RowLabel = style(FontWeight.Normal, 13.7f)
    val RowValue = style(FontWeight.SemiBold, 13.7f)
    val RowValueStrong = style(FontWeight.Bold, 13.7f)
    val Button = style(FontWeight.SemiBold, 14.8f)
    val Body = style(FontWeight.Normal, 14.8f)
    val Error = style(FontWeight.Medium, 11.6f)
}

internal object WithdrawColors {
    val CardBorder = Color(0xFFE2D5BA)
    val Chip = Color(0xFFEDE0C2)
    val StatusIconBg = Color(0xFFECE2C8)
    val StatusIcon = Color(0xFF7F7760)
    val PendingChip = Color(0xFFE9C46A)
    val OnPendingChip = Color(0xFF4B3600)
    /** Sticky Bottom Bar: rgba(254,247,230,0.97). */
    val BottomBar = Color(0xF7FEF7E6)
    val Error = Color(0xFFBA1A1A)
}

internal val WithdrawCardShape = RoundedCornerShape(16.dp)
internal val WithdrawButtonShape = RoundedCornerShape(12.dp)

/** Satu baris label–nilai di kartu Rincian (27:199) dan ringkasan pencairan (27:227). */
@Composable
internal fun WithdrawDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = WithdrawTypography.RowValue,
    valueColor: Color = TemuKaryaColors.TextPrimary
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(label, style = WithdrawTypography.RowLabel, color = TemuKaryaColors.TextBody, maxLines = 1)
        Text(
            text = value,
            style = valueStyle,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
