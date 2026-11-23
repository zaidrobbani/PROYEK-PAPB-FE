package com.example.proyek_papb_fe.feature.auth.reset_link_sent.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun ResendCountdownText(
    secondsLeft: Int,
    onResend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val canResend = secondsLeft <= 0
    val text = buildAnnotatedString {
        append("Belum menerima email? ")
        if (canResend) {
            withStyle(SpanStyle(color = colors.primary, fontWeight = FontWeight.SemiBold)) {
                append("Kirim ulang")
            }
        } else {
            append("Kirim ulang dalam %02d:%02d".format(secondsLeft / 60, secondsLeft % 60))
        }
    }

    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (canResend) Modifier.clickable(role = Role.Button, onClick = onResend)
                else Modifier
            ),
        color = colors.outline,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall.withFrameMetrics(
            fontWeight = FontWeight.Normal,
            fontSize = 12.7.sp,
            lineHeight = 1.4.em
        )
    )
}
