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

/** Chip filter Figma (535:1081): Semua · Desain Kemasan · Logo · Konten. */
private val FigmaFilters = listOf(PortfolioCategory.PACKAGING, PortfolioCategory.LOGO, PortfolioCategory.SOCIAL_CONTENT)

private fun PortfolioItem.toCardUi() = PortfolioCardUi(id, title, category.label, description, link, coverRes, coverUri)

/** "https://behance.net/x/" → "behance.net/x" (tampilan kartu Figma tanpa skema). */
private fun normalizeLink(link: String): String =
    link.trim().removePrefix("https://").removePrefix("http://").removeSuffix("/")

/**
 * Container Kelola Portofolio (Figma 535:1081). Daftar dari [FreelancerProfileStore];
 * hapus lewat sheet konfirmasi (535:1285).
 */
@Composable
fun PortfolioListContainer(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val portfolio = FreelancerProfileStore.portfolio
    // Kategori Figma + kategori lain yang benar-benar dipakai karya.
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
            pendingDeleteId?.let(FreelancerProfileStore::deletePortfolio)
            pendingDeleteId = null
        },
        onDismissDelete = { pendingDeleteId = null },
        onBack = onBack
    )
}

/**
 * Container Tambah Portofolio (535:1163, [portfolioId] null) / Edit Portofolio (535:1222).
 * Cover dipilih lewat Photo Picker dan disimpan sebagai Uri lokal (tanpa unggah ke server).
 * [onDone] dipanggil setelah simpan atau hapus untuk kembali ke Kelola Portofolio.
 */
@Composable
fun PortfolioFormContainer(
    portfolioId: String?,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
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
    // Keahlian terkait diambil dari Profil Profesional; keahlian lama karya tetap bisa dilepas.
    val skillOptions = (FreelancerProfileStore.profile.skills + form.skills).distinct()

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
            } else {
                FreelancerProfileStore.savePortfolio(
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
                onDone()
            }
        },
        onDeleteClick = { showDelete = true },
        onConfirmDelete = {
            showDelete = false
            existing?.let { FreelancerProfileStore.deletePortfolio(it.id) }
            onDone()
        },
        onDismissDelete = { showDelete = false },
        onBack = onBack
    )
}
