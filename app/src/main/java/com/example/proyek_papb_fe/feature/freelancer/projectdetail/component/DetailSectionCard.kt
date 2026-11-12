package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

internal val DetailCardShape = RoundedCornerShape(16.dp)

/** Bayangan tipis Figma detail: drop-shadow 0 1.056 1.056 rgba(0,0,0,0.05). */
internal fun Modifier.detailSubtleShadow(shape: Shape): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 1.056.dp,
        color = TemuKaryaColors.ShadowSubtle,
        offset = DpOffset(0.dp, 1.056.dp)
    )
)

/**
 * Kartu section berjudul + ikon: "Scope & Description Section" (17:4295)
 * dan "Required Skills Tags" (17:4356).
 */
@Composable
internal fun DetailSectionCard(
    @DrawableRes iconRes: Int,
    title: String,
    titleStyle: TextStyle,
    contentSpacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .detailSubtleShadow(DetailCardShape)
            .clip(DetailCardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null
            )
            Text(
                text = title,
                style = titleStyle,
                color = TemuKaryaColors.TextHeading
            )
        }
        content()
    }
}
