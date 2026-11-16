package com.example.proyek_papb_fe.feature.umkm.payment.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.payment.container.PaymentContainer
import com.example.proyek_papb_fe.feature.umkm.payment.container.PaymentProofContainer
import kotlinx.serialization.Serializable

/** Figma: Detail Pembayaran QRIS (17:1267), pembayaran UMKM untuk project [projectId]. */
@Serializable
data class PaymentRoute(val projectId: Int)

/** Figma: Bukti Pembayaran Berhasil (17:3177) untuk pembayaran [paymentId]. */
@Serializable
data class PaymentProofRoute(val paymentId: String)

fun NavController.navigateToPayment(projectId: Int, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(PaymentRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToPaymentProof(paymentId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(PaymentProofRoute(paymentId)) { launchSingleTop = true; builder() }

fun NavGraphBuilder.paymentScreens(
    onBack: () -> Unit,
    onPaid: (paymentId: String) -> Unit
) {
    composable<PaymentRoute> { entry ->
        PaymentContainer(projectId = entry.toRoute<PaymentRoute>().projectId, onBack = onBack, onPaid = onPaid)
    }
    composable<PaymentProofRoute> { entry ->
        PaymentProofContainer(paymentId = entry.toRoute<PaymentProofRoute>().paymentId, onBack = onBack)
    }
}
