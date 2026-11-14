package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val ButtonHeight = 51.dp

/** Figma: Sticky Bottom Bar (26:86) — "Batalkan" + "Lanjut Project". */
@Composable
internal fun NegotiationActionBar(
    onCancelClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NegotiationColors.BottomBarScrim)
            .drawBehind {
                drawLine(
                    color = NegotiationColors.Outline,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 17.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .clip(NegotiationButtonShape)
                .border(1.5.dp, NegotiationColors.Danger, NegotiationButtonShape)
                .clickable(onClick = onCancelClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                tint = NegotiationColors.Danger,
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = "Batalkan",
                style = NegotiationTypography.Button,
                color = NegotiationColors.Danger,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .clip(NegotiationButtonShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(onClick = onContinueClick)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TemuKaryaColors.OnAction,
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = "Lanjut Proyek",
                style = NegotiationTypography.Button,
                color = TemuKaryaColors.OnAction,
                maxLines = 1
            )
        }
    }
}
