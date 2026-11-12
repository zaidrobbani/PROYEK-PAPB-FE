package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.AgreementSummaryCard
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationColors

/** Isi "Ringkasan Kesepakatan" (dari status penawaran project) untuk ruang chat tahap negosiasi. */
data class ChatNegotiationUi(
    val projectValue: String,
    val duration: String,
    val output: String,
    val paymentMethod: String
)

/** Figma 438:772 "Info Pembatalan": Regular 11.5 / 16, tanpa latar & border. */
private val InfoStyle = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Normal,
    fontSize = 11.5.sp,
    lineHeight = 16.sp
)

/**
 * Figma: Card / Ringkasan Kesepakatan di "Ruang Chat (Freelancer) · Negosiasi" (438:772).
 * Kartu yang sama persis dengan Negosiasi 26:38 ([AgreementSummaryCard] dipakai ulang),
 * ditambah info pembatalan tanpa biaya sebagai bagian terakhir di dalam kartu.
 */
@Composable
internal fun NegotiationSummaryCard(
    negotiation: ChatNegotiationUi,
    modifier: Modifier = Modifier
) {
    AgreementSummaryCard(
        projectValue = negotiation.projectValue,
        duration = negotiation.duration,
        output = negotiation.output,
        paymentMethod = negotiation.paymentMethod,
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.BgCanvas)
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 4.dp),
        footer = { CancellationInfo() }
    )
}

@Composable
private fun CancellationInfo() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(R.drawable.ic_verified_user),
            contentDescription = null,
            tint = TemuKaryaColors.Olive,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(15.dp)
        )
        Text(
            text = "Sebelum pembayaran, kedua pihak masih bisa melanjutkan atau membatalkan project tanpa biaya.",
            style = InfoStyle,
            color = NegotiationColors.OnGreen
        )
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun NegotiationSummaryCardPreview() {
    NegotiationSummaryCard(ChatNegotiationUi("Rp 1.500.000", "14 hari kalender", "2 alternatif logo + mockup pouch", "QRIS (simulasi)"))
}
