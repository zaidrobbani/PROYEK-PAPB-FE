package com.example.proyek_papb_fe.feature.freelancer.profilemanage.container
import com.example.proyek_papb_fe.core.permission.rememberDeviceAccess
import com.example.proyek_papb_fe.core.permission.DeviceAccess

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ProfessionalProfileErrors
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ProfessionalProfileForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ProfessionalProfileScreen
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.FreelancerProfileViewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.freelancerProfileViewModelFactory
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

private val PrimaryFieldOptions = listOf(
    "Desain Grafis & Branding",
    "Desain Kemasan",
    "Fotografi Produk",
    "Konten Media Sosial",
    "Pengembangan Web"
)

private const val SnackbarMillis = 4_000L

private fun FreelancerProfile.toForm() = ProfessionalProfileForm(
    initials = initials,
    photoUri = photoUri,
    fullName = fullName,
    headline = headline,
    bio = bio,
    primaryField = primaryField,
    skills = skills,
    rateDigits = if (startingRate > 0) startingRate.toString() else "",
    city = city,
    isAvailableForWork = isAvailableForWork,
    acceptsRemoteWork = acceptsRemoteWork
)

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

private fun validate(form: ProfessionalProfileForm): ProfessionalProfileErrors {
    val headline = form.headline.trim()
    return ProfessionalProfileErrors(
        fullName = when {
            form.fullName.isBlank() -> "Nama lengkap wajib diisi."
            form.fullName.trim().length > 100 -> "Maksimal 100 karakter."
            else -> null
        },
        headline = when {
            headline.isEmpty() -> "Headline profesional wajib diisi."
            headline.length > FreelancerProfile.HEADLINE_MAX -> "Maksimal ${FreelancerProfile.HEADLINE_MAX} karakter (saat ini ${headline.length})."
            else -> null
        },
        bio = if (form.bio.trim().length > FreelancerProfile.BIO_MAX) "Maksimal ${FreelancerProfile.BIO_MAX} karakter (saat ini ${form.bio.trim().length})." else null,
        city = if (form.city.isBlank()) "Area layanan wajib diisi." else null
    )
}

@Composable
fun ProfessionalProfileContainer(
    onBack: () -> Unit,
    viewModel: FreelancerProfileViewModel = viewModel(factory = freelancerProfileViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val deviceAccess = rememberDeviceAccess()
    val saved = FreelancerProfileStore.profile
    var form by remember { mutableStateOf(saved.toForm()) }
    var formLoaded by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val errors = if (submitted) validate(form) else ProfessionalProfileErrors()

    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && !formLoaded && uiState.errorMessage == null) {
            form = FreelancerProfileStore.profile.toForm()
            formLoaded = true
        }
    }
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            if (uiState.savedCount == 0) Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.uploadPhoto(uri.toString())
    }

    LaunchedEffect(uiState.photoMessage) {
        uiState.photoMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onPhotoMessageShown()
        }
    }
    val storedPhoto = FreelancerProfileStore.profile.photoUri
    LaunchedEffect(storedPhoto) { form = form.copy(photoUri = storedPhoto) }

    LaunchedEffect(uiState.savedCount) {
        if (uiState.savedCount > 0) {
            showSnackbar = true
            delay(SnackbarMillis)
            showSnackbar = false
        }
    }

    ProfessionalProfileScreen(
        form = form,
        errors = errors,
        showErrorBanner = submitted,
        fieldOptions = PrimaryFieldOptions,
        showSavedSnackbar = showSnackbar,
        onFormChange = { form = it },
        onPickPhoto = {
            deviceAccess.request(DeviceAccess.Gallery) {
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        },
        onSave = {
            if (validate(form).count > 0) {
                submitted = true
                showSnackbar = false
            } else if (!uiState.isSaving) {
                submitted = false
                viewModel.saveProfile(
                    fullName = form.fullName,
                    headline = form.headline.trim(),
                    bio = form.bio.trim(),
                    city = form.city.trim(),
                    rate = form.rateDigits.toLongOrNull()?.takeIf { it > 0 },
                    isAvailable = form.isAvailableForWork,
                    skills = form.skills,
                    primaryField = form.primaryField,
                    acceptsRemote = form.acceptsRemoteWork
                )
            }
        },
        onViewProfile = onBack,
        onBack = onBack,
        skillOptions = uiState.skillOptions
    )
}
