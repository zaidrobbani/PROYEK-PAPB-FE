package com.example.proyek_papb_fe.feature.umkm.payment.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.payment.container.PaymentContainer
import com.example.proyek_papb_fe.feature.umkm.payment.container.PaymentProofContainer
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRoute(val projectId: String)

@Serializable
data class PaymentProofRoute(val projectId: String)

fun NavController.navigateToPayment(projectId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(PaymentRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToPaymentProof(projectId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(PaymentProofRoute(projectId)) { launchSingleTop = true; builder() }

fun NavGraphBuilder.paymentScreens(
    onBack: () -> Unit,
    onPaid: (projectId: String) -> Unit
) {
    composable<PaymentRoute> { entry ->
        PaymentContainer(projectId = entry.toRoute<PaymentRoute>().projectId, onBack = onBack, onPaid = onPaid)
    }
    composable<PaymentProofRoute> { entry ->
        PaymentProofContainer(projectId = entry.toRoute<PaymentProofRoute>().projectId, onBack = onBack)
    }
}
