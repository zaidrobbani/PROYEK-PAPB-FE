package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/** Prominent dipakai di frame Splash, Subtle di Pilih Peran, None di Lupa Password. */
enum class AuthShadowStyle { Prominent, Subtle, None }

private val ProminentShadowColor = Color.Black.copy(alpha = 0.1f)
private val SubtleShadowColor = Color.Black.copy(alpha = 0.05f)

/** Bayangan sesuai nilai pada frame Figma Authentication. */
internal fun Modifier.authDropShadow(
    shape: Shape,
    style: AuthShadowStyle = AuthShadowStyle.Prominent
): Modifier = when (style) {
    AuthShadowStyle.Prominent -> this
        .dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 6.338.dp,
                color = ProminentShadowColor,
                spread = (-1.056).dp,
                offset = DpOffset(x = 0.dp, y = 4.226.dp)
            )
        )
        .dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 4.226.dp,
                color = ProminentShadowColor,
                spread = (-2.113).dp,
                offset = DpOffset(x = 0.dp, y = 2.113.dp)
            )
        )

    AuthShadowStyle.None -> this

    AuthShadowStyle.Subtle -> this.dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = 1.056.dp,
            color = SubtleShadowColor,
            offset = DpOffset(x = 0.dp, y = 1.056.dp)
        )
    )
}
