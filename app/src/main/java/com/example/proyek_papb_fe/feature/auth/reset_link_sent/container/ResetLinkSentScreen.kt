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
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.EmailLinkPurpose
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.viewmodel.EmailLinkSentViewModel
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

@Composable
fun ResetLinkSentScreen(
    email: String,
    purpose: EmailLinkPurpose,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmailLinkSentViewModel = viewModel(
        factory = EmailLinkSentViewModel.factory(AppContainer.authRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }

    ResetLinkSentContent(
        email = email,
        purpose = purpose,
        resentCount = uiState.resentCount,
        onResend = { viewModel.resend(email, purpose) },
        onBackToLogin = onBackToLogin,
        modifier = modifier
    )
}

@Composable
private fun ResetLinkSentContent(
    email: String,
    purpose: EmailLinkPurpose,
    resentCount: Int,
    onResend: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsLeft by rememberSaveable { mutableIntStateOf(ResendCooldownSeconds) }

    LaunchedEffect(resentCount) {
        if (resentCount > 0) secondsLeft = ResendCooldownSeconds
    }

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
                text = when (purpose) {
                    EmailLinkPurpose.RESET_PASSWORD -> "Tautan untuk mengatur ulang kata sandi sudah dikirim ke"
                    EmailLinkPurpose.VERIFY_EMAIL -> "Akun berhasil dibuat. Buka tautan verifikasi yang sudah dikirim ke email berikut, lalu login"
                },
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
                onResend = onResend
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ResetLinkSentScreenPreview() {
    PROYEKPAPBFETheme {
        ResetLinkSentContent(
            email = "rina.pratiwi@kulinerbandung.id",
            purpose = EmailLinkPurpose.RESET_PASSWORD,
            resentCount = 0,
            onResend = {},
            onBackToLogin = {}
        )
    }
}
