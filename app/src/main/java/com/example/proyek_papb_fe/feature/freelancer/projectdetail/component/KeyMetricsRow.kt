package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Key Metrics Bento Row (17:4257) – Anggaran & Tenggat. */
@Composable
internal fun KeyMetricsRow(
    budget: String,
    deadline: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        MetricCard(
            label = "Anggaran",
            value = budget,
            iconRes = R.drawable.ic_payments,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "Tenggat",
            value = deadline,
            iconRes = R.drawable.ic_calendar,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .detailSubtleShadow(DetailCardShape)
            .clip(DetailCardShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .padding(10.564.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextBody
            )
            Image(
                painter = painterResource(iconRes),
                contentDescription = null
            )
        }
        Text(
            text = value,
            style = TemuKaryaTypography.MetricValue,
            color = TemuKaryaColors.TextHeading,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
