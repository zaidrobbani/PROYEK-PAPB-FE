package com.example.proyek_papb_fe.feature.auth.forgot_password.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterErrorText
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

private const val EmailLabel = "Alamat Email"

@Composable
fun ForgotPasswordEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = EmailLabel,
            color = colors.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.7.sp,
                lineHeight = 1.4.em
            )
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = EmailLabel
                    if (error != null) error(error)
                },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
                .withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.8.sp,
                    lineHeight = 1.4.em
                )
                .copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = { onSubmit() }),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .background(colors.surfaceContainerLowest, shape)
                        .border(1.dp, colors.outlineVariant, shape)
                        .padding(horizontal = 17.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_symbol_mail),
                        contentDescription = null,
                        tint = colors.outline,
                        modifier = Modifier.size(21.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                }
            }
        )
        if (error != null) {
            RegisterErrorText(message = error)
        }
    }
}
