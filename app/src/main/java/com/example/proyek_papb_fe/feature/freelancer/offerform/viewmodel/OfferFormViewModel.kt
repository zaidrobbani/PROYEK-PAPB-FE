package com.example.proyek_papb_fe.feature.freelancer.offerform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.feature.freelancer.data.toFreelancerProject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OfferFormUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val project: FreelancerProject? = null,
    val budget: Long = 0,
    val isSubmitting: Boolean = false,
    val message: String? = null,
    val submitted: Boolean = false
)

class OfferFormViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OfferFormUiState())
    val uiState: StateFlow<OfferFormUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val project = projectRepository.detail(projectId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        budget = project.budget,
                        project = project.toFreelancerProject(masterDataRepository.categoryName(project.categoryId))
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun submit(price: Long, durationDays: Int, message: String) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                projectRepository.apply(projectId, price, durationDays, message)
                _uiState.update { it.copy(isSubmitting = false, submitted = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val field = (e as? ApiException)?.fieldErrors?.values?.firstOrNull()
                _uiState.update { it.copy(isSubmitting = false, message = field ?: e.userMessage()) }
            }
        }
    }

    fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onSubmittedHandled() = _uiState.update { it.copy(submitted = false) }

    companion object {
        fun factory(
            projectId: String,
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { OfferFormViewModel(projectId, projectRepository, masterDataRepository) }
        }
    }
}
