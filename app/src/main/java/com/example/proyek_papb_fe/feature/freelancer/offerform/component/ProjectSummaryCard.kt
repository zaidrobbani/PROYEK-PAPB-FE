package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Section - Mini Ringkasan Proyek Dilamar (17:4409). */
@Composable
internal fun ProjectSummaryCard(
    title: String,
    daysLeft: Int,
    clientName: String,
    clientInitials: String,
    clientRating: Double,
    clientLocation: String,
    budget: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offerSubtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceCard, CardShape)
            .padding(CardPadding),
        verticalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SmallGap)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_schedule),
                contentDescription = null,
                tint = TemuKaryaColors.TextBody,
                modifier = Modifier.size(13.205.dp)
            )
            Text(
                text = "$daysLeft hari lagi",
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextBody
            )
        }

        Text(
            text = title,
            style = TemuKaryaTypography.SectionTitle,
            color = TemuKaryaColors.TextHeading,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SmallGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(33.805.dp)
                    .background(AvatarOverlay, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = clientInitials,
                    style = TemuKaryaTypography.LabelSemiBold,
                    color = TemuKaryaColors.TextHeading
                )
            }
            Spacer(Modifier.width(8.451.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clientName,
                    style = TemuKaryaTypography.LabelSemiBold,
                    color = TemuKaryaColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SmallGap)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = TemuKaryaColors.TextHeading,
                        modifier = Modifier.size(width = 11.444.dp, height = 10.872.dp)
                    )
                    Text(
                        text = "$clientRating, $clientLocation",
                        style = TemuKaryaTypography.CaptionMedium,
                        color = TemuKaryaColors.TextBody,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(8.451.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Pagu Klien",
                    style = TemuKaryaTypography.CaptionMedium,
                    color = TemuKaryaColors.TextBody,
                    textAlign = TextAlign.End
                )
                Text(
                    text = budget,
                    style = TemuKaryaTypography.SubsectionTitle,
                    color = TemuKaryaColors.TextHeading,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
