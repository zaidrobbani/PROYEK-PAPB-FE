package com.example.proyek_papb_fe.feature.umkm.business_profile.container
import com.example.proyek_papb_fe.core.permission.rememberDeviceAccess
import com.example.proyek_papb_fe.core.permission.DeviceAccess

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.business_profile.viewmodel.BusinessProfileViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.rememberCityLocator
import com.example.proyek_papb_fe.core.ui.components.LocationFillButton
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaErrorBanner
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaFormField
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaPhotoCard
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaSectionTitle
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaSnackbar
import com.example.proyek_papb_fe.feature.umkm.business_profile.component.KelolaStickyActionBar
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val BusinessNameMaxLength = 60

private const val PhotoMaxBytes = 15L * 1024 * 1024
private val PhotoMimeTypes = arrayOf("image/jpeg", "image/png", "image/webp")

internal data class BusinessProfileErrors(
    val businessName: String? = null,
    val category: String? = null,
    val ownerName: String? = null,
    val city: String? = null
) {
    val count: Int get() = listOfNotNull(businessName, category, ownerName, city).size
}

internal fun validateBusinessProfile(businessName: String, category: String, ownerName: String, city: String) =
    BusinessProfileErrors(
        businessName = when {
            businessName.isBlank() -> "Nama usaha wajib diisi."
            businessName.trim().length > BusinessNameMaxLength ->
                "Maksimal $BusinessNameMaxLength karakter (saat ini ${businessName.trim().length})."
            else -> null
        },
        category = "Kategori usaha wajib diisi.".takeIf { category.isBlank() },
        ownerName = when {
            ownerName.isBlank() -> "Nama pemilik wajib diisi."
            ownerName.trim().length > 100 -> "Maksimal 100 karakter."
            else -> null
        },
        city = "Kota atau kabupaten wajib diisi.".takeIf { city.isBlank() }
    )

@Composable
fun BusinessProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BusinessProfileViewModel = viewModel(
        factory = BusinessProfileViewModel.factory(
            AppContainer.profileRepository,
            AppContainer.authRepository,
            AppContainer.profilePhotoRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val deviceAccess = rememberDeviceAccess()
    val scope = rememberCoroutineScope()
    val saved = UmkmAccount.profile
    var initialized by rememberSaveable { mutableStateOf(false) }
    var businessName by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var ownerName by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    val cityLocator = rememberCityLocator { found -> city = listOfNotNull(found.city, found.province).joinToString(", ") }
    val photoUri = UmkmAccount.profile.photoUri
    var photoError by rememberSaveable { mutableStateOf<String?>(null) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val scroll = rememberScrollState()

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && !initialized && uiState.loadError == null) {
            ownerName = uiState.ownerName
            uiState.profile?.let {
                businessName = it.businessName
                category = it.businessCategory
                city = it.city
            }
            initialized = true
        }
    }
    LaunchedEffect(uiState.saveError) {
        uiState.saveError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onSaveErrorShown()
        }
    }

    if (uiState.isLoading) {
        LoadingContent(modifier.background(MaterialTheme.colorScheme.background))
        return
    }
    uiState.loadError?.let { message ->
        ErrorContent(message = message, onRetry = viewModel::load, modifier = modifier.background(MaterialTheme.colorScheme.background))
        return
    }

    val localErrors = if (submitted) validateBusinessProfile(businessName, category, ownerName, city) else BusinessProfileErrors()
    val errors = BusinessProfileErrors(
        businessName = localErrors.businessName ?: uiState.fieldErrors["businessName"],
        category = localErrors.category ?: uiState.fieldErrors["category"],
        ownerName = localErrors.ownerName ?: uiState.fieldErrors["ownerName"],
        city = localErrors.city ?: uiState.fieldErrors["city"]
    )
    val initials = ownerName.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifEmpty { saved.initials }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val type = context.contentResolver.getType(uri)
        val size = fileSize(context, uri)
        photoError = when {
            type !in PhotoMimeTypes -> "Format foto harus JPG, PNG, atau WebP."
            size != null && size > PhotoMaxBytes -> "Ukuran foto melebihi 15 MB."
            else -> null
        }
        if (photoError == null) viewModel.uploadPhoto(uri.toString())
    }

    LaunchedEffect(uiState.savedCount) {
        if (uiState.savedCount > 0) {
            showSnackbar = true
            delay(4_000)
            showSnackbar = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            AuthAppBar(title = "Kelola Profil Usaha", onBack = onBack)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(scroll)
                    .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errors.count > 0) KelolaErrorBanner(errors.count)
                KelolaPhotoCard(
                    initials = initials,
                    photoUri = photoUri,
                    description = if (uiState.isUploadingPhoto) "Mengunggah foto..." else "Tampil di Profil Saya. JPG atau PNG, maks. 2 MB.",
                    onPickPhoto = { deviceAccess.request(DeviceAccess.Gallery) { photoPicker.launch(PhotoMimeTypes) } }
                )
                photoError?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                KelolaSectionTitle("Informasi Usaha", "Ditampilkan kepada freelancer di project dan percakapanmu.")
                KelolaFormField(
                    label = "Nama usaha",
                    value = businessName,
                    onValueChange = { businessName = it },
                    placeholder = "Nama usaha",
                    helper = "${businessName.trim().length}/$BusinessNameMaxLength karakter · tampil di project dan pengajuan",
                    error = errors.businessName,
                    capitalization = KeyboardCapitalization.Words
                )
                KelolaFormField(
                    label = "Kategori usaha",
                    value = category,
                    onValueChange = { category = it },
                    placeholder = "Contoh: Kuliner",
                    error = errors.category,
                    capitalization = KeyboardCapitalization.Words
                )
                KelolaSectionTitle("Pemilik Usaha")
                KelolaFormField(
                    label = "Nama pemilik",
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    placeholder = "Nama pemilik",
                    helper = "Juga mengubah nama akunmu di TEMUKARYA.",
                    error = errors.ownerName,
                    capitalization = KeyboardCapitalization.Words
                )
                KelolaSectionTitle("Alamat Usaha")
                KelolaFormField(
                    label = "Kota atau kabupaten",
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "Contoh: Bandung, Jawa Barat",
                    helper = "Membantu freelancer mengetahui lokasi usahamu. Ketuk ikon lokasi untuk mengisi otomatis.",
                    error = errors.city,
                    trailingIcon = R.drawable.ic_symbol_location_on,
                    capitalization = KeyboardCapitalization.Words,
                    trailingContent = {
                        LocationFillButton(cityLocator, tint = MaterialTheme.colorScheme.primary, iconRes = R.drawable.ic_symbol_location_on)
                    }
                )
            }
            KelolaStickyActionBar(
                label = if (uiState.isSaving) "Menyimpan..." else if (uiState.isNew) "Simpan Profil Usaha" else "Simpan Perubahan",
                onClick = {
                    submitted = true
                    if (validateBusinessProfile(businessName, category, ownerName, city).count == 0) {
                        viewModel.save(ownerName, businessName, category, city)
                    } else {
                        scope.launch { scroll.animateScrollTo(0) }
                    }
                }
            )
        }
        KelolaSnackbar(
            visible = showSnackbar,
            message = "Perubahan profil usaha disimpan",
            actionLabel = "Lihat Profil",
            onAction = onBack,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp)
        )
    }
}

private fun fileSize(context: Context, uri: Uri): Long? =
    context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else null
    }
