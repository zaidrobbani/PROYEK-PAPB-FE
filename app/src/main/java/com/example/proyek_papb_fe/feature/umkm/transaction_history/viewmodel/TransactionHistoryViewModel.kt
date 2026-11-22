package com.example.proyek_papb_fe.feature.umkm.transaction_history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.domain.model.FundStatusCode
import com.example.proyek_papb_fe.core.domain.model.PaymentHistoryItem
import com.example.proyek_papb_fe.core.domain.model.PaymentStatusCode
import com.example.proyek_papb_fe.core.domain.model.TransactionGroup
import com.example.proyek_papb_fe.feature.umkm.data.FundStatus
import com.example.proyek_papb_fe.feature.umkm.data.PaymentStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionHistoryUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val transactions: List<UmkmTransaction> = emptyList()
)

/** Riwayat Transaksi UMKM (`GET /payments/history`). Tab & pencarian difilter lokal dari daftar lengkap. */
class TransactionHistoryViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val history = paymentRepository.history(TransactionGroup.ALL)
                _uiState.update { it.copy(isLoading = false, transactions = history.items.map { item -> item.toTransaction() }) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    private fun PaymentHistoryItem.toTransaction(): UmkmTransaction {
        val fund = when (fundStatus) {
            FundStatusCode.HELD -> FundStatus.Tertahan
            FundStatusCode.RELEASED -> FundStatus.Dilepas
            else -> null
        }
        return UmkmTransaction(
            code = transactionNumber ?: paymentId,
            projectId = projectId,
            projectTitle = projectTitle,
            freelancerName = freelancerName,
            paidAtMillis = occurredAtMillis ?: 0L,
            amount = amount,
            paymentStatus = when (status) {
                PaymentStatusCode.BERHASIL -> PaymentStatus.Berhasil
                PaymentStatusCode.GAGAL -> PaymentStatus.Gagal
                PaymentStatusCode.DIBATALKAN -> PaymentStatus.Dibatalkan
                else -> PaymentStatus.Menunggu
            },
            fundStatus = fund,
            iconRes = when (fund) {
                FundStatus.Tertahan -> R.drawable.ic_transaction_escrow
                FundStatus.Dilepas -> R.drawable.ic_transaction_done
                null -> R.drawable.ic_transaction_qris
            }
        )
    }

    companion object {
        fun factory(paymentRepository: PaymentRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { TransactionHistoryViewModel(paymentRepository) }
        }
    }
}
