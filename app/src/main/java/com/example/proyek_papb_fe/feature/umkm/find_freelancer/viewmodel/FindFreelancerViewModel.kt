package com.example.proyek_papb_fe.feature.umkm.find_freelancer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.domain.model.FreelancerSearchFilter
import com.example.proyek_papb_fe.feature.umkm.data.DirectoryFreelancer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FindFreelancerUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val freelancers: List<DirectoryFreelancer> = emptyList()
)

/** Cari Freelancer (`GET /freelancers`, freelancer AVAILABLE). Kata kunci & filter cepat diterapkan lokal. */
class FindFreelancerViewModel(
    private val freelancerRepository: FreelancerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FindFreelancerUiState())
    val uiState: StateFlow<FindFreelancerUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val page = freelancerRepository.search(FreelancerSearchFilter(), limit = 100)
                val list = page.items.map { f ->
                    DirectoryFreelancer(
                        id = f.userId,
                        name = f.name,
                        photoUrl = f.photoUrl,
                        role = f.professionalTitle,
                        category = f.professionalTitle,
                        city = f.city,
                        lastActiveMinutes = 0,
                        rating = f.averageRating ?: 0.0,
                        reviewCount = f.reviewCount,
                        ratingNote = if (f.reviewCount > 0) "${f.reviewCount} ulasan" else "Belum ada ulasan",
                        highlightTitle = "${f.experienceCount} pengalaman kerja",
                        highlightSubtitle = f.city,
                        highlightIsExperience = true,
                        experienceYears = null,
                        skills = f.skills.take(3).map { it.name },
                        moreSkillCount = (f.skills.size - 3).coerceAtLeast(0),
                        startingPrice = f.servicePriceMin ?: 0
                    )
                }
                _uiState.update { it.copy(isLoading = false, freelancers = list) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    companion object {
        fun factory(freelancerRepository: FreelancerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { FindFreelancerViewModel(freelancerRepository) }
        }
    }
}
