package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/**
 * Figma: kartu "Ringkasan Kesepakatan" (26:38). Dipakai bersama oleh layar Negosiasi dan
 * Ruang Chat tahap negosiasi. [footer] (opsional) tampil sebagai bagian terakhir kartu,
 * dipisah garis setelah "Metode bayar" (Figma 438:772: info pembatalan tanpa biaya).
 */
@Composable
internal fun AgreementSummaryCard(
    projectValue: String,
    duration: String,
    output: String,
    paymentMethod: String,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceContainer, NegotiationCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_description_rounded),
                contentDescription = null,
                tint = TemuKaryaColors.ActionPrimary,
                modifier = Modifier.size(21.dp)
            )
            Text(
                text = "Ringkasan Kesepakatan",
                style = NegotiationTypography.SectionTitle,
                color = TemuKaryaColors.TextHeading
            )
        }

        SummaryRow("Nilai proyek", projectValue, NegotiationTypography.RowValueBold)
        SummaryRow("Durasi pengerjaan", duration, NegotiationTypography.RowValue)
        SummaryRow("Output", output, NegotiationTypography.RowValue)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NegotiationColors.Outline)
        )
        SummaryRow("Metode bayar", paymentMethod, NegotiationTypography.RowValue)
        if (footer != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(NegotiationColors.Outline)
            )
            footer()
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueStyle: TextStyle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = NegotiationTypography.RowLabel,
            color = TemuKaryaColors.TextBody
        )
        // Nilai panjang (mis. Output) membungkus ke baris berikut, tetap rata kanan.
        Text(
            text = value,
            style = valueStyle,
            color = TemuKaryaColors.TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(start = 12.dp)
        )
    }
}
