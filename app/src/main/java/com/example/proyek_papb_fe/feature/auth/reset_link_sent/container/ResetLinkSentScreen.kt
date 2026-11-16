package com.example.proyek_papb_fe.feature.auth.reset_link_sent.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.component.ResendCountdownText
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.component.ResetLinkSentEmailChip
import kotlinx.coroutines.delay

private const val ResendCooldownSeconds = 59

/**
 * Hanya UI lokal. Teks "sudah dikirim" mengikuti Figma, tetapi tidak ada email yang
 * benar-benar dikirim karena backend di luar scope. "Kirim ulang" hanya mengulang hitung mundur.
 */
@Composable
fun ResetLinkSentScreen(
    email: String,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsLeft by rememberSaveable { mutableIntStateOf(ResendCooldownSeconds) }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1_000)
            secondsLeft -= 1
        }
    }

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(start = 17.dp, end = 17.dp, top = 64.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(SageBadge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_symbol_mark_email_read),
                    contentDescription = null,
                    tint = colors.secondary,
                    modifier = Modifier.size(42.dp)
                )
            }
            Text(
                text = "Cek email kamu",
                modifier = Modifier.fillMaxWidth(),
                color = colors.onTertiaryContainer,
                textAlign = TextAlign.Center,
                style = typography.headlineSmall.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.2.sp,
                    lineHeight = 1.4.em
                )
            )
            Text(
                text = "Tautan untuk mengatur ulang kata sandi sudah dikirim ke",
                modifier = Modifier.fillMaxWidth(),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = typography.bodyMedium.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.8.sp,
                    lineHeight = 1.4.em
                )
            )
            ResetLinkSentEmailChip(email = email)
            AuthPrimaryButton(
                text = "Kembali ke Login",
                onClick = onBackToLogin,
                shadowStyle = AuthShadowStyle.None,
                height = 51.dp
            )
            ResendCountdownText(
                secondsLeft = secondsLeft,
                onResend = { secondsLeft = ResendCooldownSeconds }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ResetLinkSentScreenPreview() {
    PROYEKPAPBFETheme {
        ResetLinkSentScreen(
            email = "rina.pratiwi@kulinerbandung.id",
            onBackToLogin = {}
        )
    }
}
