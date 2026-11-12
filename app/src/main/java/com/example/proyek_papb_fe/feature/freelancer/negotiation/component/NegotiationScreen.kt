package com.example.proyek_papb_fe.feature.freelancer.negotiation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.NegotiationMessage
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Data tampilan (sudah diformat) untuk ruang negosiasi satu project. */
data class NegotiationUiState(
    val projectTitle: String,
    val client: NegotiationParty,
    val freelancer: NegotiationParty,
    val projectValue: String,
    val duration: String,
    val output: String,
    val paymentMethod: String,
    val dayLabel: String,
    val messages: List<NegotiationMessage>
)

/**
 * Figma: Negosiasi (Freelancer) (26:2).
 * Seluruh konten di-scroll; top bar dan bottom bar tetap. Frame ini tidak punya
 * kolom ketik pesan — percakapan hanya ditampilkan.
 */
@Composable
fun NegotiationScreen(
    state: NegotiationUiState,
    onBack: () -> Unit,
    onCancelClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Negosiasi", onBack = onBack)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    // Figma: Main pt 84 (app bar 64 + 20), px 17, pb 120 (bottom bar 80 + 40).
                    .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NegotiationHeaderCard(
                    projectTitle = state.projectTitle,
                    client = state.client,
                    freelancer = state.freelancer
                )
                AgreementSummaryCard(
                    projectValue = state.projectValue,
                    duration = state.duration,
                    output = state.output,
                    paymentMethod = state.paymentMethod
                )
                DaySeparatorChip(label = state.dayLabel)
                state.messages.forEach { message ->
                    NegotiationMessageBubble(
                        message = message,
                        clientInitials = state.client.initials
                    )
                }
                CancellationInfoCard()
            }

            NegotiationActionBar(
                onCancelClick = onCancelClick,
                onContinueClick = onContinueClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/** Figma: kartu info (26:77) — pembatalan tanpa biaya sebelum pembayaran. */
@Composable
private fun CancellationInfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NegotiationColors.InfoCard, NegotiationCardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_verified_user),
            contentDescription = null,
            tint = TemuKaryaColors.Olive,
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = "Sebelum pembayaran, kedua pihak masih bisa melanjutkan atau membatalkan project tanpa biaya.",
            style = NegotiationTypography.Info,
            color = NegotiationColors.OnGreen,
            modifier = Modifier.weight(1f)
        )
    }
}
