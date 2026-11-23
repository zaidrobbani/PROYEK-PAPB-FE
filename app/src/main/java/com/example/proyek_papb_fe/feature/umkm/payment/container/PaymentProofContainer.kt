package com.example.proyek_papb_fe.feature.umkm.payment.container

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentProofScreen
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentProofUiState
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.payment.viewmodel.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.proyek_papb_fe.core.ui.components.rememberServerImageUri

private fun transactionTime(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis)) + " WIB"

/** Bukti pembayaran: data dari `GET /projects/{id}/payment` (transaction_number, paid_at, nominal). */
@Composable
fun PaymentProofContainer(
    projectId: String,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModel.factory(projectId, AppContainer.paymentRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val payment = uiState.payment
    if (payment == null) {
        if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        return
    }
    val transactionNumber = payment.transactionNumber ?: payment.id
    PaymentProofScreen(
        state = PaymentProofUiState(
            paymentId = transactionNumber,
            paidAt = transactionTime(payment.paidAtMillis ?: payment.createdAtMillis ?: System.currentTimeMillis()),
            total = formatRupiah(payment.totalAmount),
            contractValue = formatRupiah(payment.grossAmount),
            methodLabel = payment.paymentMethodLabel,
            projectTitle = payment.projectTitle,
            freelancerName = payment.freelancer.name,
            freelancerRole = shortRole(payment.freelancer.subtitle.orEmpty()),
            freelancerPhotoUri = rememberServerImageUri(payment.freelancer.photoUrl),
            umkmName = payment.umkm.subtitle ?: payment.umkm.name,
            umkmOwner = payment.umkm.name
        ),
        onBack = onBack,
        onCopyPaymentId = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Nomor Transaksi", transactionNumber))
            Toast.makeText(context, "Nomor transaksi disalin", Toast.LENGTH_SHORT).show()
        }
    )
}
