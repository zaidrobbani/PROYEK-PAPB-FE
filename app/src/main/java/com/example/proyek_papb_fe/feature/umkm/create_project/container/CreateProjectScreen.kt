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
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import kotlinx.coroutines.launch

/**
 * Figma: padding bawah konten 101.415 di belakang bilah tombol setinggi 67.61.
 * Bilah diletakkan di bawah area scroll (bukan menimpanya) agar field yang digulir otomatis
 * tidak tertutup, jadi sisanya 33.805 dipakai sebagai jarak akhir konten.
 */
private val BottomBarClearance = 101.415.dp - 67.61.dp

/**
 * Form Buat Project (Blueprint TK-F-U-02). Publikasi hanya simulasi lokal:
 * tidak ada data yang dikirim ke server, dan [onPublished] menerima hasil form.
 *
 * [editProjectId] terisi = mode Edit Project (Blueprint TK-F-U-04). Figma tidak punya frame
 * Edit Project, jadi form yang sama dipakai ulang dengan judul "Edit Project" dan tombol
 * "Simpan Perubahan" (tanpa tombol draf).
 *
 * [copyFromProjectId] terisi = Pesan Lagi: isian disalin dari pesanan sebelumnya kecuali batas
 * waktu (tanggal lama sudah lewat), lalu dipublikasikan sebagai project baru berstatus Terbuka.
 */
@Composable
fun CreateProjectScreen(
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublished: (CreateProjectDraft) -> Unit,
    modifier: Modifier = Modifier,
    editProjectId: String? = null,
    copyFromProjectId: String? = null
) {
    val editing = editProjectId?.let { UmkmProjectStore.project(it) }
    val isEdit = editProjectId != null
    val source = editing ?: copyFromProjectId?.let { UmkmProjectStore.project(it) }
    val state = rememberCreateProjectFormState(initial = source, keepDeadline = isEdit)
    val errors = state.visibleErrors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val requesters = remember { ProjectFormField.entries.associateWith { BringIntoViewRequester() } }

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
        } else if (isEdit) {
            val saved = editProjectId != null && UmkmProjectStore.update(
                projectId = editProjectId,
                title = draft.title,
                category = draft.category,
                description = draft.description,
                skills = draft.skills,
                budget = draft.budget,
                deadlineUtcMillis = draft.deadlineUtcMillis,
                attachments = draft.attachments
            )
            Toast.makeText(
                context,
                if (saved) "Perubahan project disimpan (simulasi). Belum tersimpan di server."
                else "Project sudah masuk tahap pengerjaan dan tidak bisa diubah.",
                Toast.LENGTH_LONG
            ).show()
            if (saved) onPublished(draft)
        } else {
            // Disimpan ke penyimpanan lokal sementara agar muncul di Project Saya (status Terbuka).
            UmkmProjectStore.publish(
                title = draft.title,
                category = draft.category,
                description = draft.description,
                skills = draft.skills,
                budget = draft.budget,
                deadlineUtcMillis = draft.deadlineUtcMillis,
                attachments = draft.attachments
            )
            Toast.makeText(
                context,
                "Project dipublikasikan (simulasi). Belum tersimpan di server.",
                Toast.LENGTH_LONG
            ).show()
            onPublished(draft)
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
                // Banner ajakan membuat project baru tidak relevan saat mengedit.
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
                        options = DummyProjectCategories,
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
                        onAddCustomSkill = state::addCustomSkill
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
                onSaveDraft = onSaveDraft,
                onPublish = onPublish,
                publishLabel = if (isEdit) "Simpan Perubahan" else "Publikasikan Project Sekarang",
                showSaveDraft = !isEdit
            )
        }
    }
}

/** Membaca nama dan ukuran file dari penyedia dokumen Android. */
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

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CreateProjectScreenPreview() {
    PROYEKPAPBFETheme {
        CreateProjectScreen(onBack = {}, onSaveDraft = {}, onPublished = {})
    }
}
