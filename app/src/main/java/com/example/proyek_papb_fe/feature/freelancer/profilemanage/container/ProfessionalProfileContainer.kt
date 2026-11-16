package com.example.proyek_papb_fe.feature.freelancer.profilemanage.container

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

/** Pilihan "Bidang keahlian utama" (Figma hanya menampilkan nilai terpilih). */
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

/** "Andi Ramadhan" → "AR". */
private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

/** Validasi sesuai Figma state Validasi (534:1067). */
private fun validate(form: ProfessionalProfileForm): ProfessionalProfileErrors {
    val headline = form.headline.trim()
    return ProfessionalProfileErrors(
        fullName = if (form.fullName.isBlank()) "Nama lengkap wajib diisi." else null,
        headline = when {
            headline.isEmpty() -> "Headline profesional wajib diisi."
            headline.length > FreelancerProfile.HEADLINE_MAX -> "Maksimal ${FreelancerProfile.HEADLINE_MAX} karakter (saat ini ${headline.length})."
            else -> null
        },
        bio = if (form.bio.trim().length > FreelancerProfile.BIO_MAX) "Maksimal ${FreelancerProfile.BIO_MAX} karakter (saat ini ${form.bio.trim().length})." else null,
        city = if (form.city.isBlank()) "Area layanan wajib diisi." else null
    )
}

/**
 * Container Kelola Profil Profesional (Figma 534:961). Isian diawali dari
 * [FreelancerProfileStore]; "Simpan Perubahan" memvalidasi lalu menyimpan ke store sehingga
 * Profil langsung menampilkan data baru. Foto dipilih lewat Photo Picker (lokal, tanpa unggah).
 */
@Composable
fun ProfessionalProfileContainer(onBack: () -> Unit) {
    val saved = FreelancerProfileStore.profile
    var form by remember { mutableStateOf(saved.toForm()) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var savedTick by rememberSaveable { mutableIntStateOf(0) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val errors = if (submitted) validate(form) else ProfessionalProfileErrors()

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) form = form.copy(photoUri = uri.toString())
    }

    LaunchedEffect(savedTick) {
        if (savedTick > 0) {
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
        onPickPhoto = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onSave = {
            if (validate(form).count > 0) {
                submitted = true
                showSnackbar = false
            } else {
                submitted = false
                val name = form.fullName.trim()
                FreelancerProfileStore.updateProfile(
                    FreelancerProfileStore.profile.copy(
                        fullName = name,
                        initials = initialsOf(name),
                        headline = form.headline.trim(),
                        bio = form.bio.trim(),
                        primaryField = form.primaryField,
                        skills = form.skills,
                        startingRate = form.rateDigits.toLongOrNull() ?: 0L,
                        city = form.city.trim(),
                        isAvailableForWork = form.isAvailableForWork,
                        acceptsRemoteWork = form.acceptsRemoteWork,
                        photoUri = form.photoUri
                    )
                )
                form = FreelancerProfileStore.profile.toForm()
                savedTick++
            }
        },
        onViewProfile = onBack,
        onBack = onBack
    )
}
