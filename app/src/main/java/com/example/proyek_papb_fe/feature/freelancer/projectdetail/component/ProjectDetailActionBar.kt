package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val ButtonHeight = 50.708.dp
private val ButtonShape = RoundedCornerShape(12.dp)

/**
 * Figma: Sticky Bottom Floating Action Bar (17:4379).
 * Latar 95% krem + bayangan ke atas; backdrop blur Figma tidak direplikasi.
 */
@Composable
internal fun ProjectDetailActionBar(
    onChatClick: () -> Unit,
    onSubmitOfferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 16.903.dp,
                    color = TemuKaryaColors.ShadowCard,
                    offset = DpOffset(0.dp, (-4.226).dp)
                )
            )
            .background(TemuKaryaColors.ActionBarScrim)
            .padding(horizontal = 16.903.dp, vertical = 12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ButtonHeight)
                .detailSubtleShadow(ButtonShape)
                .clip(ButtonShape)
                .background(TemuKaryaColors.SurfaceContainer)
                .clickable(onClickLabel = "Tanya klien via chat", onClick = onChatClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_chat),
                contentDescription = "Tanya klien via chat"
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .dropShadow(
                    shape = ButtonShape,
                    shadow = Shadow(
                        radius = 6.338.dp,
                        spread = (-1.056).dp,
                        color = TemuKaryaColors.ShadowButton,
                        offset = DpOffset(0.dp, 4.226.dp)
                    )
                )
                .dropShadow(
                    shape = ButtonShape,
                    shadow = Shadow(
                        radius = 4.226.dp,
                        spread = (-2.113).dp,
                        color = TemuKaryaColors.ShadowButton,
                        offset = DpOffset(0.dp, 2.113.dp)
                    )
                )
                .clip(ButtonShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(onClick = onSubmitOfferClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ajukan Penawaran",
                style = TemuKaryaTypography.ButtonLabel,
                color = TemuKaryaColors.OnAction
            )
        }
    }
}
