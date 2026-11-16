package com.example.proyek_papb_fe.feature.freelancer.profilemanage.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.model.MonthYear
import com.example.proyek_papb_fe.core.data.model.WorkExperience
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ExperienceForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ExperienceFormErrors
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ExperienceFormScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ExperienceItemUi
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.ExperienceListScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.component.label
import java.util.Calendar
import java.util.TimeZone

/** Bulan berjalan (WIB) untuk "Sekarang" dan batas pemilih bulan. */
private fun currentMonth(): MonthYear {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
    return MonthYear(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
}

/** Lama bekerja inklusif: Mar 2022 – Des 2023 → "1 thn 10 bln" (sesuai Figma). */
private fun durationLabel(start: MonthYear, end: MonthYear): String {
    val months = (end.totalMonths - start.totalMonths + 1).coerceAtLeast(1)
    val y = months / 12
    val m = months % 12
    return listOfNotNull(if (y > 0) "$y thn" else null, if (m > 0) "$m bln" else null).joinToString(" ")
}

private fun WorkExperience.toItemUi(now: MonthYear) = ExperienceItemUi(
    id = id,
    position = position,
    organization = organization,
    period = "${start.label()} – ${end?.label() ?: "Sekarang"} · ${durationLabel(start, end ?: now)}",
    description = description
)

/** Container Kelola Pengalaman (Figma 538:1235); hapus lewat sheet konfirmasi (538:1424). */
@Composable
fun ExperienceListContainer(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val now = currentMonth()
    val experiences = FreelancerProfileStore.experiences
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    ExperienceListScreen(
        totalYearsLabel = FreelancerProfileStore.profile.experienceYearsLabel.replace("thn", "tahun"),
        items = experiences.map { it.toItemUi(now) },
        pendingDelete = pendingDeleteId?.let { id -> experiences.firstOrNull { it.id == id }?.toItemUi(now) },
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onDeleteClick = { pendingDeleteId = it },
        onConfirmDelete = {
            pendingDeleteId?.let(FreelancerProfileStore::deleteExperience)
            pendingDeleteId = null
        },
        onDismissDelete = { pendingDeleteId = null },
        onBack = onBack
    )
}

private fun validate(f: ExperienceForm) = ExperienceFormErrors(
    position = if (f.position.isBlank()) "Posisi wajib diisi." else null,
    organization = if (f.organization.isBlank()) "Perusahaan/organisasi wajib diisi." else null,
    employmentType = if (f.employmentType == null) "Pilih jenis pekerjaan." else null,
    start = if (f.start == null) "Pilih bulan mulai." else null,
    end = when {
        f.isCurrent -> null
        f.end == null -> "Pilih bulan selesai."
        f.start != null && f.end != null && f.end < f.start -> "Harus setelah Mulai."
        else -> null
    }
)

private val ExperienceFormErrors.hasAny: Boolean
    get() = listOfNotNull(position, organization, employmentType, start, end).isNotEmpty()

/**
 * Container Tambah Pengalaman (538:1305, [experienceId] null) / Edit Pengalaman (538:1363).
 * [onDone] dipanggil setelah simpan atau hapus untuk kembali ke Kelola Pengalaman.
 */
@Composable
fun ExperienceFormContainer(
    experienceId: String?,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val existing = experienceId?.let(FreelancerProfileStore::experience)
    var form by remember {
        mutableStateOf(
            existing?.let {
                ExperienceForm(it.position, it.organization, it.employmentType, it.start, it.end, it.end == null, it.description)
            } ?: ExperienceForm()
        )
    }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    ExperienceFormScreen(
        isEdit = existing != null,
        form = form,
        errors = if (submitted) validate(form) else ExperienceFormErrors(),
        maxMonth = currentMonth(),
        showDeleteSheet = showDelete,
        onFormChange = { form = it },
        onSave = {
            if (validate(form).hasAny) {
                submitted = true
            } else {
                FreelancerProfileStore.saveExperience(
                    WorkExperience(
                        id = existing?.id.orEmpty(),
                        position = form.position.trim(),
                        organization = form.organization.trim(),
                        employmentType = form.employmentType!!,
                        start = form.start!!,
                        end = if (form.isCurrent) null else form.end,
                        description = form.description.trim()
                    )
                )
                onDone()
            }
        },
        onDeleteClick = { showDelete = true },
        onConfirmDelete = {
            showDelete = false
            existing?.let { FreelancerProfileStore.deleteExperience(it.id) }
            onDone()
        },
        onDismissDelete = { showDelete = false },
        onBack = onBack
    )
}
