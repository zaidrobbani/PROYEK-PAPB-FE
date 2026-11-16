package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val ButtonHeight = 50.708.dp

/**
 * Figma: Sticky Bottom Action Bar (17:4587).
 * Latar krem 95% + bayangan ke atas; backdrop blur Figma tidak direplikasi
 * (sama seperti action bar Detail Project).
 */
@Composable
internal fun OfferFormActionBar(
    onSaveDraftClick: () -> Unit,
    onSubmitClick: () -> Unit,
    isSubmitEnabled: Boolean,
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
            .padding(horizontal = 16.903.dp, vertical = 8.451.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(123.251.dp)
                .height(ButtonHeight)
                .clip(FieldShape)
                .background(TemuKaryaColors.SurfaceContainer)
                .clickable(onClick = onSaveDraftClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Simpan Draf",
                style = TemuKaryaTypography.LabelSemiBold,
                color = TemuKaryaColors.TextBody
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(ButtonHeight)
                .dropShadow(
                    shape = FieldShape,
                    shadow = Shadow(
                        radius = 6.338.dp,
                        spread = (-1.056).dp,
                        color = TemuKaryaColors.ShadowButton,
                        offset = DpOffset(0.dp, 4.226.dp)
                    )
                )
                .dropShadow(
                    shape = FieldShape,
                    shadow = Shadow(
                        radius = 4.226.dp,
                        spread = (-2.113).dp,
                        color = TemuKaryaColors.ShadowButton,
                        offset = DpOffset(0.dp, 2.113.dp)
                    )
                )
                .clip(FieldShape)
                .background(
                    if (isSubmitEnabled) TemuKaryaColors.ActionPrimary
                    else TemuKaryaColors.ActionPrimary.copy(alpha = 0.4f)
                )
                .clickable(enabled = isSubmitEnabled, onClick = onSubmitClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Kirim Penawaran",
                style = TemuKaryaTypography.ButtonLabel,
                color = TemuKaryaColors.OnAction
            )
        }
    }
}
