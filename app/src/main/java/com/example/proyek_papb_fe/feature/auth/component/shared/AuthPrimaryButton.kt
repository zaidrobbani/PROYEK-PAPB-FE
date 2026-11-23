package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shadowStyle: AuthShadowStyle = AuthShadowStyle.Prominent,
    height: Dp = 50.708.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
    iconSpacing: Dp = 8.dp,
    isLoading: Boolean = false
) {
    val shape = RoundedCornerShape(12.dp)

    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .authDropShadow(shape, shadowStyle),
        shape = shape,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            return@Button
        }
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(iconSpacing))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.79.sp,
                lineHeight = 19.015.sp,
                letterSpacing = 0.sp
            )
        )
    }
}
