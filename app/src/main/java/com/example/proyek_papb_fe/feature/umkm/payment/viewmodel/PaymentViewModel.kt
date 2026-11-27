package com.example.proyek_papb_fe.feature.umkm.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.domain.model.PaymentDetail
import com.example.proyek_papb_fe.core.domain.model.PaymentStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class PaymentVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val payment: PaymentDetail? = null,
    val isProcessing: Boolean = false,
    val message: String? = null,
    val paidProjectId: String? = null
)

class PaymentViewModel(
    private val projectId: String,
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentVmState())
    val uiState: StateFlow<PaymentVmState> = _uiState.asStateFlow()

    private var pendingIdempotencyKey: String? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val payment = paymentRepository.payment(projectId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        payment = payment,
                        paidProjectId = projectId.takeIf { payment.isPaid }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun pay() {
        val current = _uiState.value.payment ?: return
        if (_uiState.value.isProcessing || current.isPaid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            try {
                val expired = current.expiresAtMillis?.let { it <= System.currentTimeMillis() } == true
                if (current.status == PaymentStatusCode.GAGAL || expired) {
                    _uiState.update { it.copy(payment = paymentRepository.regenerateQris(projectId)) }
                    pendingIdempotencyKey = null
                }
                val key = pendingIdempotencyKey ?: UUID.randomUUID().toString().also { pendingIdempotencyKey = it }
                val result = paymentRepository.simulatePay(projectId, key)
                pendingIdempotencyKey = null
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        payment = result,
                        paidProjectId = projectId.takeIf { result.isPaid },
                        message = if (result.isPaid) null else "Pembayaran gagal, silakan coba lagi"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val api = e as? ApiException
                if (api?.isNetworkError != true) pendingIdempotencyKey = null
                _uiState.update { it.copy(isProcessing = false, message = e.userMessage()) }
                if (api?.code in RefreshCodes) load()
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onPaidHandled() = _uiState.update { it.copy(paidProjectId = null) }

    companion object {
        private val RefreshCodes = setOf("QRIS_EXPIRED", "QRIS_REFRESH_REQUIRED", "PAYMENT_ALREADY_PAID")

        fun factory(projectId: String, paymentRepository: PaymentRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { PaymentViewModel(projectId, paymentRepository) }
            }
    }
}
