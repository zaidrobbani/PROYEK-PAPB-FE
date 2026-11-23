package com.example.proyek_papb_fe.feature.umkm.business_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.ProfilePhotoRepository
import com.example.proyek_papb_fe.core.domain.model.UmkmBusinessProfile
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessProfileUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val ownerName: String = "",
    val profile: UmkmBusinessProfile? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    /** Bertambah setiap simpan berhasil (memicu snackbar). */
    val savedCount: Int = 0,
    val fieldErrors: Map<String, String> = emptyMap(),
    val isUploadingPhoto: Boolean = false
) {
    val isNew: Boolean get() = profile == null
}

class BusinessProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val photoRepository: ProfilePhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessProfileUiState())
    val uiState: StateFlow<BusinessProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            try {
                val profile = profileRepository.umkmProfile()
                val owner = authRepository.currentUser()?.name.orEmpty()
                UmkmAccount.update(owner, profile)
                UmkmAccount.updatePhoto(photoRepository.currentLocalUri())
                _uiState.update { it.copy(isLoading = false, ownerName = owner, profile = profile) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, loadError = e.userMessage()) }
            }
        }
    }

    fun save(ownerName: String, businessName: String, category: String, city: String) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null, fieldErrors = emptyMap()) }
            try {
                val owner = ownerName.trim()
                val savedOwner = if (owner != _uiState.value.ownerName) authRepository.updateName(owner).name else owner
                _uiState.update { it.copy(ownerName = savedOwner) }
                // Figma hanya punya field kota di section "Alamat Usaha"; BE mewajibkan business_address.
                val saved = profileRepository.saveUmkmProfile(
                    businessName = businessName,
                    category = category,
                    address = city,
                    city = city
                )
                UmkmAccount.update(_uiState.value.ownerName, saved)
                _uiState.update { it.copy(isSaving = false, profile = saved, savedCount = it.savedCount + 1) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val fieldErrors = (e as? ApiException)?.fieldErrors.orEmpty().mapKeys { (key, _) ->
                    when (key) {
                        "business_name" -> "businessName"
                        "business_category" -> "category"
                        "name" -> "ownerName"
                        "city", "business_address" -> "city"
                        else -> key
                    }
                }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        fieldErrors = fieldErrors,
                        saveError = if (fieldErrors.isEmpty()) e.userMessage() else null
                    )
                }
            }
        }
    }

    /** Foto langsung diunggah saat dipilih (dikompres ke WebP), terpisah dari tombol Simpan. */
    fun uploadPhoto(uri: String) {
        if (_uiState.value.isUploadingPhoto) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true) }
            try {
                UmkmAccount.updatePhoto(photoRepository.upload(uri))
                _uiState.update { it.copy(isUploadingPhoto = false, saveError = "Foto profil diperbarui") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploadingPhoto = false, saveError = e.userMessage()) }
            }
        }
    }

    fun onSaveErrorShown() = _uiState.update { it.copy(saveError = null) }

    companion object {
        fun factory(
            profileRepository: ProfileRepository,
            authRepository: AuthRepository,
            photoRepository: ProfilePhotoRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { BusinessProfileViewModel(profileRepository, authRepository, photoRepository) }
        }
    }
}
