package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Section - Form Input Nilai Penawaran & Kalkulator Fee (17:4436). */
@Composable
internal fun OfferAmountCard(
    /** Nilai penawaran = pagu klien; tidak bisa diubah freelancer di layar ini. */
    amount: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offerSubtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceWhite, CardShape)
            .padding(CardPadding),
        verticalArrangement = Arrangement.spacedBy(SmallGap)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nilai Penawaran Anda",
                style = TemuKaryaTypography.SubsectionTitle,
                color = TemuKaryaColors.TextHeading
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.113.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = TemuKaryaColors.Olive,
                    modifier = Modifier.size(width = 9.86.dp, height = 12.941.dp)
                )
                Text(
                    text = "Budget klien",
                    style = TemuKaryaTypography.CaptionMedium,
                    color = TemuKaryaColors.Olive
                )
            }
        }

        OfferAmountField(amount = amount)

        FeeBreakdown(amount = amount)
    }
}

/**
 * Figma: Input Field (17:4446), dibuat read-only: nominal tetap = pagu klien.
 * Teks biasa (bukan TextField) sehingga tidak ada keyboard, kursor, maupun seleksi.
 */
@Composable
private fun OfferAmountField(amount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 47.128.dp)
            .background(TemuKaryaColors.SurfaceCard, FieldShape)
            .padding(horizontal = 12.677.dp, vertical = 10.564.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rp",
            style = TemuKaryaTypography.SectionTitle,
            color = TemuKaryaColors.TextBody,
            modifier = Modifier.padding(end = SmallGap)
        )
        Text(
            text = formatThousands(amount),
            style = OfferFormTypography.OfferInput,
            color = TemuKaryaColors.TextHeading,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "Nilai penawaran ${formatRupiah(amount)}, mengikuti budget klien" }
        )
        Icon(
            painter = painterResource(R.drawable.ic_check_circle),
            contentDescription = null,
            tint = TemuKaryaColors.Olive,
            modifier = Modifier.size(17.607.dp)
        )
    }
}

/** Figma: Breakdown Card Transparansi Biaya (17:4454). Nilai dihitung dari nominal input. */
@Composable
private fun FeeBreakdown(amount: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceContainer, InnerShape)
            .padding(start = 12.677.dp, end = 12.677.dp, top = 21.128.dp, bottom = 12.677.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SmallGap),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nilai Penawaran",
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextBody
            )
            Text(
                text = formatRupiah(amount),
                style = TemuKaryaTypography.LabelSemiBold,
                color = TemuKaryaColors.TextPrimary
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SmallGap, bottom = 8.451.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SmallGap)
            ) {
                Text(
                    text = "Biaya Layanan Platform ($PlatformFeePercent%)",
                    style = TemuKaryaTypography.BodyRegular,
                    color = TemuKaryaColors.TextBody
                )
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = TemuKaryaColors.TextPlaceholder,
                    modifier = Modifier.size(11.444.dp)
                )
            }
            Text(
                text = "-${formatRupiah(platformFee(amount))}",
                style = TemuKaryaTypography.LabelSemiBold,
                color = TemuKaryaColors.TextBody
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.056.dp)
                .background(BreakdownDivider)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.451.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.754.dp)) {
                Text(
                    text = "Estimasi Bersih Diterima",
                    style = TemuKaryaTypography.LabelSemiBold,
                    color = TemuKaryaColors.Olive
                )
                Text(
                    text = "Langsung cair ke dompet",
                    style = TemuKaryaTypography.CaptionMedium,
                    color = TemuKaryaColors.TextBody
                )
            }
            Text(
                text = formatRupiah(netAmount(amount)),
                style = TemuKaryaTypography.Amount,
                color = TemuKaryaColors.Olive
            )
        }
    }
}
