package com.example.proyek_papb_fe.feature.umkm.create_project.container

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.create_project.viewmodel.CreateProjectViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.umkm.create_project.component.AttachedFileItem
import com.example.proyek_papb_fe.feature.umkm.create_project.component.AttachmentDropzone
import com.example.proyek_papb_fe.feature.umkm.create_project.component.BudgetField
import com.example.proyek_papb_fe.feature.umkm.create_project.component.CategoryDropdown
import com.example.proyek_papb_fe.feature.umkm.create_project.component.CreateProjectBottomBar
import com.example.proyek_papb_fe.feature.umkm.create_project.component.CreateProjectIntroBanner
import com.example.proyek_papb_fe.feature.umkm.create_project.component.DeadlineDurationNote
import com.example.proyek_papb_fe.feature.umkm.create_project.component.DeadlineField
import com.example.proyek_papb_fe.feature.umkm.create_project.component.DescriptionField
import com.example.proyek_papb_fe.feature.umkm.create_project.component.ProjectFieldError
import com.example.proyek_papb_fe.feature.umkm.create_project.component.ProjectFieldNote
import com.example.proyek_papb_fe.feature.umkm.create_project.component.ProjectFormSection
import com.example.proyek_papb_fe.feature.umkm.create_project.component.ProjectTextInput
import com.example.proyek_papb_fe.feature.umkm.create_project.component.SkillSelector
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import kotlinx.coroutines.launch

private val BottomBarClearance = 101.415.dp - 67.61.dp

@Composable
fun CreateProjectScreen(
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublished: (CreateProjectDraft) -> Unit,
    modifier: Modifier = Modifier,
    editProjectId: String? = null,
    copyFromProjectId: String? = null,
    viewModel: CreateProjectViewModel = viewModel(
        factory = CreateProjectViewModel.factory(
            AppContainer.projectRepository,
            AppContainer.masterDataRepository,
            editProjectId,
            copyFromProjectId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEdit = editProjectId != null
    val state = rememberCreateProjectFormState()
    var sourceApplied by rememberSaveable { mutableStateOf(false) }
    var lastDraft by remember { mutableStateOf<CreateProjectDraft?>(null) }
    val localErrors = state.visibleErrors
    val errors = CreateProjectErrors(
        title = localErrors.title ?: uiState.fieldErrors["title"],
        category = localErrors.category ?: uiState.fieldErrors["category"],
        description = localErrors.description ?: uiState.fieldErrors["description"],
        skills = localErrors.skills ?: uiState.fieldErrors["skills"],
        budget = localErrors.budget ?: uiState.fieldErrors["budget"],
        deadline = localErrors.deadline ?: uiState.fieldErrors["deadline"]
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val requesters = remember { ProjectFormField.entries.associateWith { BringIntoViewRequester() } }

    LaunchedEffect(uiState.skills) {
        if (uiState.skills.isNotEmpty()) {
            val names = uiState.skills.map { it.name }
            state.skillOptions.clear()
            state.skillOptions.addAll(names + state.selectedSkills.filterNot { it in names })
        }
    }
    LaunchedEffect(uiState.source) {
        val source = uiState.source
        if (source != null && !sourceApplied) {
            state.fillFrom(source, keepDeadline = isEdit)
            sourceApplied = true
        }
    }
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.savedProjectId) {
        if (uiState.savedProjectId != null) {
            viewModel.onSavedHandled()
            lastDraft?.let(onPublished)
        }
    }

    if (uiState.isLoading) {
        LoadingContent(modifier.background(MaterialTheme.colorScheme.background))
        return
    }
    uiState.loadError?.let { message ->
        ErrorContent(message, onRetry = viewModel::load, modifier = modifier.background(MaterialTheme.colorScheme.background))
        return
    }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        state.attachmentError = null
        uris.forEach { uri ->
            val attachment = readAttachment(context, uri)
            when {
                attachment == null -> state.attachmentError = "File tidak dapat dibaca."
                context.contentResolver.getType(uri) !in AttachmentMimeTypes ->
                    state.attachmentError = "Format ${attachment.name} tidak didukung. Gunakan JPG, PNG, atau PDF."
                attachment.sizeBytes > AttachmentMaxBytes ->
                    state.attachmentError = "${attachment.name} melebihi batas 10MB."
                state.attachments.none { it.uri == attachment.uri } -> state.attachments.add(attachment)
            }
        }
    }

    val onPublish: () -> Unit = {
        val draft = state.submit()
        if (draft == null) {
            state.firstErrorField()?.let { field ->
                scope.launch { requesters.getValue(field).bringIntoView() }
            }
        } else {
            lastDraft = draft
            viewModel.submit(draft)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = if (isEdit) "Edit Project" else "Buat Project", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 16.903.dp,
                        end = 16.903.dp,
                        top = 3.61.dp,
                        bottom = BottomBarClearance
                    ),
                verticalArrangement = Arrangement.spacedBy(16.903.dp)
            ) {
                if (!isEdit) CreateProjectIntroBanner()
                ProjectFormSection(
                    label = "Judul Project",
                    modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Title))
                ) {
                    ProjectTextInput(
                        value = state.title,
                        onValueChange = { state.title = it },
                        contentDescription = "Judul Project",
                        error = errors.title,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )
                    ProjectFieldError(errors.title)
                }
                ProjectFormSection(
                    label = "Kategori Utama",
                    modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Category))
                ) {
                    CategoryDropdown(
                        selected = state.category,
                        options = uiState.categories.map { it.name },
                        onSelected = { state.category = it },
                        error = errors.category
                    )
                    ProjectFieldError(errors.category)
                }
                ProjectFormSection(
                    label = "Deskripsi Kebutuhan",
                    modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Description))
                ) {
                    DescriptionField(
                        value = state.description,
                        onValueChange = { state.description = it },
                        maxLength = DescriptionMaxLength,
                        error = errors.description
                    )
                    ProjectFieldError(errors.description)
                }
                ProjectFormSection(
                    label = "Keahlian yang Dibutuhkan",
                    modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Skills))
                ) {
                    SkillSelector(
                        options = state.skillOptions,
                        selected = state.selectedSkills,
                        onToggle = state::toggleSkill,
                        onAddCustomSkill = { raw ->
                            val known = viewModel.findSkillName(raw)
                            if (known != null) state.addCustomSkill(known)
                            else viewModel.showMessage("Keahlian \"${raw.trim()}\" belum tersedia. Pilih dari daftar keahlian TEMUKARYA.")
                        }
                    )
                    ProjectFieldError(errors.skills)
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.677.dp)) {
                    ProjectFormSection(
                        label = "Estimasi Budget (Rp)",
                        modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Budget))
                    ) {
                        BudgetField(
                            digits = state.budgetDigits,
                            onDigitsChange = { state.budgetDigits = it },
                            error = errors.budget
                        )
                        ProjectFieldError(errors.budget)
                        ProjectFieldNote(
                            iconRes = R.drawable.ic_create_project_shield,
                            text = AnnotatedString("Dana ditahan TEMUKARYA sampai kamu menyetujui hasil."),
                            modifier = Modifier.padding(top = 6.338.dp, end = 73.737.dp),
                            verticalAlignment = Alignment.Top
                        )
                    }
                    ProjectFormSection(
                        label = "Batas Waktu Pengerjaan",
                        modifier = Modifier.bringIntoViewRequester(requesters.getValue(ProjectFormField.Deadline))
                    ) {
                        DeadlineField(
                            deadlineUtcMillis = state.deadlineUtcMillis,
                            onDeadlineChange = { state.deadlineUtcMillis = it },
                            error = errors.deadline
                        )
                        ProjectFieldError(errors.deadline)
                        state.deadlineUtcMillis?.let { deadline ->
                            DeadlineDurationNote(
                                deadlineUtcMillis = deadline,
                                modifier = Modifier.padding(top = 6.338.dp)
                            )
                        }
                    }
                }
                ProjectFormSection(
                    label = "Lampiran & Dokumen Pendukung",
                    spacing = 8.451.dp
                ) {
                    AttachmentDropzone(onPickFiles = { filePicker.launch(AttachmentMimeTypes) })
                    state.attachments.forEach { attachment ->
                        AttachedFileItem(
                            attachment = attachment,
                            onRemove = { state.attachments.remove(attachment) }
                        )
                    }
                    ProjectFieldError(state.attachmentError)
                }
            }
            CreateProjectBottomBar(
                onSaveDraft = {
                    // BE belum punya status draf: project langsung TERBUKA saat dipublikasikan.
                    viewModel.showMessage("Simpan draf belum tersedia. Lengkapi form lalu publikasikan project.")
                    onSaveDraft()
                },
                onPublish = onPublish,
                publishLabel = when {
                    uiState.isSubmitting -> "Menyimpan..."
                    isEdit -> "Simpan Perubahan"
                    else -> "Publikasikan Project Sekarang"
                },
                showSaveDraft = !isEdit
            )
        }
    }
}

private fun readAttachment(context: Context, uri: Uri): ProjectAttachment? =
    context.contentResolver
        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
        ?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
            val size = if (cursor.isNull(sizeIndex)) 0L else cursor.getLong(sizeIndex)
            ProjectAttachment(uri = uri.toString(), name = name ?: "file", sizeBytes = size)
        }

