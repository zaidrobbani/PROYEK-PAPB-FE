package com.example.proyek_papb_fe.feature.freelancer.profilemanage.container

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.model.PortfolioCategory
import com.example.proyek_papb_fe.core.data.model.PortfolioItem
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioCardUi
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioFilterUi
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioFormErrors
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioFormScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.PortfolioListScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.FreelancerProfileViewModel
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel.freelancerProfileViewModelFactory
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

private val FigmaFilters = listOf(PortfolioCategory.PACKAGING, PortfolioCategory.LOGO, PortfolioCategory.SOCIAL_CONTENT)

private fun PortfolioItem.toCardUi() = PortfolioCardUi(id, title, category.label, description, link, coverRes, coverUri)

private fun normalizeLink(link: String): String =
    link.trim().removePrefix("https://").removePrefix("http://").removeSuffix("/")

@Composable
fun PortfolioListContainer(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: FreelancerProfileViewModel = viewModel(factory = freelancerProfileViewModelFactory())
) {
    LaunchedEffect(Unit) { viewModel.load() }
    val portfolio = FreelancerProfileStore.portfolio
    val categories = FigmaFilters + portfolio.map { it.category }.distinct().filterNot { it in FigmaFilters }
    var selectedFilter by rememberSaveable { mutableIntStateOf(0) }
    if (selectedFilter > categories.size) selectedFilter = 0
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val shown = if (selectedFilter == 0) portfolio else portfolio.filter { it.category == categories[selectedFilter - 1] }
    PortfolioListScreen(
        totalCount = portfolio.size,
        filters = listOf(PortfolioFilterUi("Semua (${portfolio.size})", selectedFilter == 0)) +
            categories.mapIndexed { i, c -> PortfolioFilterUi(c.filterLabel, selectedFilter == i + 1) },
        items = shown.map { it.toCardUi() },
        pendingDelete = pendingDeleteId?.let { id -> portfolio.firstOrNull { it.id == id }?.toCardUi() },
        onFilterClick = { selectedFilter = it },
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onDeleteClick = { pendingDeleteId = it },
        onConfirmDelete = {
            pendingDeleteId?.let(viewModel::deletePortfolio)
            pendingDeleteId = null
        },
        onDismissDelete = { pendingDeleteId = null },
        onBack = onBack
    )
}

@Composable
fun PortfolioFormContainer(
    portfolioId: String?,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: FreelancerProfileViewModel = viewModel(factory = freelancerProfileViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.savedCount) { if (uiState.savedCount > 0) onDone() }
    val existing = portfolioId?.let(FreelancerProfileStore::portfolioItem)
    var form by remember {
        mutableStateOf(
            existing?.let {
                PortfolioForm(it.title, it.category, it.description, it.link, it.skills, it.coverRes, it.coverUri)
            } ?: PortfolioForm()
        )
    }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    val validate = { f: PortfolioForm ->
        PortfolioFormErrors(
            title = if (f.title.isBlank()) "Judul karya wajib diisi." else null,
            category = if (f.category == null) "Pilih kategori karya." else null,
            description = if (f.description.isBlank()) "Deskripsi wajib diisi." else null
        )
    }
    val errors = if (submitted) validate(form) else PortfolioFormErrors()
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) form = form.copy(coverUri = uri.toString())
    }
    val skillOptions = (FreelancerProfileStore.profile.skills + uiState.skillOptions + form.skills).distinct()

    PortfolioFormScreen(
        isEdit = existing != null,
        form = form,
        errors = errors,
        skillOptions = skillOptions,
        showDeleteSheet = showDelete,
        onFormChange = { form = it },
        onPickCover = { coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onSave = {
            val e = validate(form)
            if (e.title != null || e.category != null || e.description != null) {
                submitted = true
            } else if (!uiState.isSaving) {
                viewModel.savePortfolio(
                    existing?.id,
                    PortfolioItem(
                        id = existing?.id.orEmpty(),
                        title = form.title.trim(),
                        category = form.category!!,
                        description = form.description.trim(),
                        link = normalizeLink(form.link),
                        skills = form.skills,
                        coverRes = form.coverRes,
                        coverUri = form.coverUri
                    )
                )
            }
        },
        onDeleteClick = { showDelete = true },
        onConfirmDelete = {
            showDelete = false
            existing?.let { viewModel.deletePortfolio(it.id) } ?: onDone()
        },
        onDismissDelete = { showDelete = false },
        onBack = onBack
    )
}
