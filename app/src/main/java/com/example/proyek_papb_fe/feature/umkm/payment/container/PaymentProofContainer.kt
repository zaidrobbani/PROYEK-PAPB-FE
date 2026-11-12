package com.example.proyek_papb_fe.feature.umkm.payment.container

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.model.PaymentStatus
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentProofScreen
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentProofUiState
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** "24 Okt 2026, 14.32 WIB". */
private fun transactionTime(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis)) + " WIB"

/**
 * Container Bukti Pembayaran (Figma 17:3177) untuk [paymentId]. Membaca pembayaran yang sama
 * dengan layar Pembayaran ([PaymentStore]), jadi nominal & project selalu sama. Hanya untuk
 * pembayaran PAID; selain itu kembali.
 */
@Composable
fun PaymentProofContainer(paymentId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val payment = PaymentStore.byId(paymentId)
    val project = payment?.let { FreelancerProjectDummyData.getById(it.projectId) }
    if (payment == null || project == null || payment.status != PaymentStatus.PAID) {
        LaunchedEffect(paymentId) { onBack() }
        return
    }
    val profile = FreelancerProfileStore.profile
    PaymentProofScreen(
        state = PaymentProofUiState(
            paymentId = payment.paymentId,
            paidAt = transactionTime(payment.paidAtMillis ?: payment.createdAtMillis),
            total = formatRupiah(payment.amount),
            contractValue = formatRupiah(payment.amount),
            methodLabel = "${payment.method.label} (simulasi)",
            projectTitle = project.title,
            freelancerName = profile.fullName,
            freelancerRole = shortRole(profile.headline),
            freelancerPhotoUri = profile.photoUri,
            umkmName = project.clientName,
            umkmOwner = project.ownerName
        ),
        onBack = onBack,
        onCopyPaymentId = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Nomor Transaksi", payment.paymentId))
            Toast.makeText(context, "Nomor transaksi disalin", Toast.LENGTH_SHORT).show()
        }
    )
}
