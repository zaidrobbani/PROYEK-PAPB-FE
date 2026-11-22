package com.example.proyek_papb_fe.feature.freelancer.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.domain.model.Wallet
import com.example.proyek_papb_fe.core.domain.model.WalletEntry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val wallet: Wallet? = null,
    val entries: List<WalletEntry> = emptyList()
)

/** Dompet freelancer (F3): saldo tersedia = dana RELEASED, saldo ditahan = dana HELD. Pencairan di luar scope MVP. */
class WalletViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletVmState())
    val uiState: StateFlow<WalletVmState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val wallet = async { paymentRepository.wallet() }
                    val entries = async { paymentRepository.walletEntries() }
                    _uiState.update { it.copy(isLoading = false, wallet = wallet.await(), entries = entries.await()) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    companion object {
        fun factory(paymentRepository: PaymentRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { WalletViewModel(paymentRepository) }
        }
    }
}
