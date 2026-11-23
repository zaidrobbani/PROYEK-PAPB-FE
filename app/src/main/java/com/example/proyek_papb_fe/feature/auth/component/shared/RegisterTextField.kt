package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R

internal fun Modifier.registerInputShadow(shape: Shape): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 2.113.dp,
        color = Color.Black.copy(alpha = 0.05f),
        offset = DpOffset(x = 0.dp, y = 1.056.dp)
    )
)

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    leadingContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    labelTrailing: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable BoxScope.() -> Unit)? = null,
    textStartPadding: Dp = 46.482.dp,
    textEndPadding: Dp = 16.903.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    withShadow: Boolean = true,
    borderColor: Color? = null,
    textColor: Color = Color.Unspecified,
    errorContent: @Composable (String) -> Unit = { RegisterErrorText(message = it) }
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.338.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = colors.onSurface,
                style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
            labelTrailing?.invoke()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.708.dp)
                .then(if (withShadow) Modifier.registerInputShadow(shape) else Modifier)
                .background(Color.White, shape)
                .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .semantics {
                        contentDescription = label
                        if (error != null) error(error)
                    },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge
                    .withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.79.sp,
                        lineHeight = 19.015.sp
                    )
                    .copy(color = textColor.takeOrElse { colors.onSurface }),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = textStartPadding, end = textEndPadding),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                }
            )
            leadingContent()
            trailingContent?.invoke(this)
        }
        if (error != null) {
            errorContent(error)
        }
    }
}

@Composable
internal fun RegisterErrorText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall.withFrameMetrics(
            fontWeight = FontWeight.Medium,
            fontSize = 11.621.sp,
            lineHeight = 14.79.sp
        )
    )
}

@Composable
fun RegisterFieldHint(
    text: String,
    @DrawableRes iconRes: Int? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.102.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Image(painter = painterResource(iconRes), contentDescription = null)
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}

@Composable
fun BoxScope.RegisterFieldIcon(
    @DrawableRes iconRes: Int,
    left: Dp,
    top: Dp,
    width: Dp,
    height: Dp
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = left, y = top)
            .size(width = width, height = height)
    )
}

@Composable
fun BoxScope.RegisterPasswordToggle(
    visible: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-4.7).dp, y = (-6.83).dp)
            .size(48.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onToggle)
            .semantics {
                contentDescription =
                    if (visible) "Sembunyikan kata sandi" else "Tampilkan kata sandi"
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(R.drawable.ic_register_eye), contentDescription = null)
        if (!visible) {
            Image(
                painter = painterResource(R.drawable.ic_register_eye_slash),
                contentDescription = null
            )
        }
    }
}

@Composable
fun BoxScope.RegisterPasswordMatchIcon() {
    Image(
        painter = painterResource(R.drawable.ic_register_match_check),
        contentDescription = "Kata sandi sama",
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-14.79).dp, y = 10.56.dp)
    )
}
