package com.example.proyek_papb_fe.feature.umkm.payment.container

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentScreen
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentUiState
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.payment.viewmodel.PaymentViewModel
import kotlinx.coroutines.delay
import java.util.Locale

internal fun shortRole(headline: String): String = headline.substringBefore(" & ").trim()

@Composable
fun PaymentContainer(
    projectId: String,
    onBack: () -> Unit,
    onPaid: (projectId: String) -> Unit,
    viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModel.factory(projectId, AppContainer.paymentRepository)
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
    LaunchedEffect(uiState.paidProjectId) {
        uiState.paidProjectId?.let {
            viewModel.onPaidHandled()
            onPaid(it)
        }
    }
    BackHandler(enabled = uiState.isProcessing) {}

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000)
        }
    }

    val payment = uiState.payment
    if (payment == null) {
        if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        return
    }
    // Countdown "Selesaikan dalam" mengikuti expires_at BE (15 menit). Setelah habis, tombol bayar membuat QRIS baru.
    val remainingSeconds = payment.expiresAtMillis?.let { ((it - now) / 1000).coerceAtLeast(0) } ?: 0
    val freelancer = payment.freelancer

    PaymentScreen(
        state = PaymentUiState(
            projectTitle = payment.projectTitle,
            freelancerName = freelancer.name,
            freelancerPhotoUri = freelancer.photoUrl,
            freelancerRating = freelancer.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
            freelancerRole = shortRole(freelancer.subtitle.orEmpty()),
            duration = "${payment.durationDays} Hari Kalender",
            countdown = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
            total = formatRupiah(payment.totalAmount),
            agreedValue = formatRupiah(payment.grossAmount),
            platformFeeLabel = "Biaya Platform TEMUKARYA (${payment.commissionPercent}%)",
            platformFee = formatRupiah(payment.platformFee),
            freelancerNet = formatRupiah(payment.freelancerAmount),
            isProcessing = uiState.isProcessing
        ),
        onBack = { if (!uiState.isProcessing) onBack() },
        onPay = viewModel::pay
    )
}
