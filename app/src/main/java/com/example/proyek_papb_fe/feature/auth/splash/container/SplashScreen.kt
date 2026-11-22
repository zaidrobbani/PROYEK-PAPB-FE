package com.example.proyek_papb_fe.feature.auth.splash.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import com.example.proyek_papb_fe.feature.auth.splash.viewmodel.SplashViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.splash.component.TemuKaryaLogoBadge

private val SplashMinContentHeight = 424.042.dp

@Composable
fun SplashScreen(
    onStartClick: () -> Unit,
    onLoggedIn: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = viewModel(
        factory = SplashViewModel.factory(AppContainer.authRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loggedInRole) {
        uiState.loggedInRole?.let { role ->
            viewModel.onNavigated()
            onLoggedIn(role)
        }
    }

    SplashContent(
        onStartClick = { if (!uiState.isCheckingSession) onStartClick() },
        modifier = modifier
    )
}

@Composable
private fun SplashContent(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val contentHeight = max(maxHeight, SplashMinContentHeight)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .height(contentHeight)
                .padding(horizontal = 16.903.dp, vertical = 25.354.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.128.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SplashBranding()
            }
            SplashFooter(onStartClick = onStartClick)
        }
    }
}

@Composable
private fun SplashBranding() {
    Column(
        modifier = Modifier
            .widthIn(max = 338.051.dp)
            .fillMaxWidth()
            .heightIn(min = 254.595.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TemuKaryaLogoBadge()
        Spacer(Modifier.height(16.903.dp))
        Text(
            text = "TEMUKARYA",
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 31.692.sp,
                lineHeight = 40.144.sp,
                letterSpacing = (-0.7923).sp
            )
        )
        Spacer(Modifier.height(4.226.dp))
        Text(
            text = "Temukan talenta, wujudkan karya",
            modifier = Modifier.padding(horizontal = 8.451.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp,
                letterSpacing = 0.sp
            )
        )
    }
}

@Composable
private fun SplashFooter(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.226.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthPrimaryButton(
            text = "Mulai",
            onClick = onStartClick,
            modifier = Modifier.widthIn(max = 405.662.dp)
        )
        Spacer(Modifier.height(15.dp))
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SplashScreenPreview() {
    PROYEKPAPBFETheme {
        SplashContent(onStartClick = {})
    }
}
