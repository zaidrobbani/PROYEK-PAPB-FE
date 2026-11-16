package com.example.proyek_papb_fe.feature.freelancer.profilemanage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.EmploymentType
import com.example.proyek_papb_fe.core.data.model.MonthYear
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val MonthShort = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

/** MonthYear(3, 2022) → "Mar 2022". */
fun MonthYear.label(): String = "${MonthShort[month - 1]} $year"

/** Satu item pengalaman (sudah diformat). */
data class ExperienceItemUi(
    val id: String,
    val position: String,
    val organization: String,
    /** "Mar 2022 – Des 2023 · 1 thn 10 bln". */
    val period: String,
    val description: String
)

/**
 * Figma: Kelola Pengalaman (538:1235), Kosong (538:1289), dan Hapus Pengalaman (Modal) (538:1424).
 */
@Composable
fun ExperienceListScreen(
    totalYearsLabel: String,
    items: List<ExperienceItemUi>,
    pendingDelete: ExperienceItemUi?,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        KelolaScaffold(title = "Kelola Pengalaman", onBack = onBack, modifier = modifier) {
            KelolaEmptyState(
                iconRes = R.drawable.ic_profile_work_history,
                title = "Belum ada pengalaman kerja",
                body = "Tambahkan pengalamanmu agar UMKM bisa menilai rekam jejak sebelum mengundangmu.",
                actionLabel = "Tambah Pengalaman",
                actionIconRes = R.drawable.ic_add,
                onAction = onAddClick
            )
        }
        return
    }
    KelolaScaffold(
        title = "Kelola Pengalaman",
        onBack = onBack,
        modifier = modifier,
        bottomBar = { KelolaStickyActionBar("Tambah Pengalaman", R.drawable.ic_add, onAddClick) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("${items.size} pengalaman kerja", style = KelolaTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
            Text("Total $totalYearsLabel · diurutkan dari yang terbaru.", style = KelolaTypography.Body, color = TemuKaryaColors.TextBody)
        }
        items.forEach { item -> ExperienceItem(item, onEdit = { onEditClick(item.id) }, onDelete = { onDeleteClick(item.id) }) }
    }
    pendingDelete?.let {
        KelolaDeleteSheet(
            title = "Hapus pengalaman ini?",
            subtitle = "${it.position} · ${it.organization}",
            body = "Pengalaman akan dihapus dari profilmu dan tidak lagi terlihat oleh UMKM.",
            confirmLabel = "Ya, Hapus Pengalaman",
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

/** Figma "Kelola/Experience Item": ikon 40, posisi, perusahaan, periode, deskripsi, Edit & Hapus bertumpuk. */
@Composable
private fun ExperienceItem(item: ExperienceItemUi, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, KelolaCardShape)
            .border(1.dp, KelolaColors.Border, KelolaCardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        KelolaIconBadge(R.drawable.ic_profile_work_history, size = 40.dp, iconSize = 19.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(item.position, style = KelolaTypography.CardTitle, color = TemuKaryaColors.TextPrimary)
            Text(item.organization, style = KelolaTypography.BodyMedium, color = TemuKaryaColors.TextBody)
            Text(item.period, style = KelolaTypography.Caption, color = KelolaColors.Muted)
            Text(item.description, style = KelolaTypography.Body, color = TemuKaryaColors.TextBody)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KelolaIconButton(R.drawable.ic_profile_edit, TemuKaryaColors.ActionPrimary, "Edit ${item.position}", onEdit)
            KelolaIconButton(R.drawable.ic_delete, KelolaColors.Error, "Hapus ${item.position}", onDelete)
        }
    }
}

/** Isian form Tambah/Edit Pengalaman. */
data class ExperienceForm(
    val position: String = "",
    val organization: String = "",
    val employmentType: EmploymentType? = null,
    val start: MonthYear? = null,
    val end: MonthYear? = null,
    val isCurrent: Boolean = false,
    val description: String = ""
)

data class ExperienceFormErrors(
    val position: String? = null,
    val organization: String? = null,
    val employmentType: String? = null,
    val start: String? = null,
    val end: String? = null
)

const val ExperienceDescriptionMax = 300

/** Figma: Tambah Pengalaman (538:1305) dan Edit Pengalaman (538:1363). */
@Composable
fun ExperienceFormScreen(
    isEdit: Boolean,
    form: ExperienceForm,
    errors: ExperienceFormErrors,
    maxMonth: MonthYear,
    showDeleteSheet: Boolean,
    onFormChange: (ExperienceForm) -> Unit,
    onSave: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var picking by rememberSaveable { mutableStateOf<String?>(null) } // "start" / "end"

    KelolaScaffold(
        title = if (isEdit) "Edit Pengalaman" else "Tambah Pengalaman",
        onBack = onBack,
        modifier = modifier,
        bottomBar = { KelolaStickyActionBar(if (isEdit) "Simpan Perubahan" else "Simpan Pengalaman", R.drawable.ic_profile_check, onSave) }
    ) {
        KelolaTextField(
            label = "Posisi / jabatan",
            value = form.position,
            onValueChange = { onFormChange(form.copy(position = it.take(60))) },
            placeholder = "Contoh: Desainer Grafis",
            error = errors.position,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
        )
        KelolaTextField(
            label = "Perusahaan / organisasi",
            value = form.organization,
            onValueChange = { onFormChange(form.copy(organization = it.take(60))) },
            placeholder = "Contoh: CV Kriya Nusantara",
            error = errors.organization,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
        )
        KelolaDropdownField(
            label = "Jenis pekerjaan",
            options = EmploymentType.entries,
            selected = form.employmentType,
            optionLabel = { it.label },
            placeholder = "Pilih jenis pekerjaan",
            onSelect = { onFormChange(form.copy(employmentType = it)) },
            helper = if (isEdit) null else "Penuh waktu, paruh waktu, freelance, atau magang.",
            error = errors.employmentType
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KelolaSelectField(
                label = "Mulai",
                value = form.start?.label(),
                placeholder = "Bulan, tahun",
                trailingIconRes = R.drawable.ic_profile_calendar,
                onClick = { picking = "start" },
                error = errors.start,
                modifier = Modifier.weight(1f)
            )
            KelolaSelectField(
                label = "Selesai",
                value = if (form.isCurrent) "Sekarang" else form.end?.label(),
                placeholder = "Bulan, tahun",
                trailingIconRes = R.drawable.ic_profile_calendar,
                onClick = { picking = "end" },
                error = errors.end,
                enabled = !form.isCurrent,
                modifier = Modifier.weight(1f)
            )
        }
        KelolaCheckboxRow("Saya masih bekerja di sini", form.isCurrent) { onFormChange(form.copy(isCurrent = it, end = if (it) null else form.end)) }
        KelolaTextField(
            label = "Deskripsi pekerjaan",
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it.take(ExperienceDescriptionMax))) },
            placeholder = "Tuliskan tanggung jawab dan hasil kerja utama.",
            helper = "${form.description.length}/$ExperienceDescriptionMax karakter",
            singleLine = false,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        if (isEdit) {
            KelolaOutlinedButton("Hapus Pengalaman", onDeleteClick, iconRes = R.drawable.ic_delete, color = KelolaColors.Error)
        }
    }

    picking?.let { which ->
        MonthYearPickerDialog(
            title = if (which == "start") "Mulai bekerja" else "Selesai bekerja",
            initial = (if (which == "start") form.start else form.end) ?: maxMonth,
            maxMonth = maxMonth,
            onPick = { picked ->
                onFormChange(if (which == "start") form.copy(start = picked) else form.copy(end = picked))
                picking = null
            },
            onDismiss = { picking = null }
        )
    }
    if (showDeleteSheet) {
        KelolaDeleteSheet(
            title = "Hapus pengalaman ini?",
            subtitle = "${form.position} · ${form.organization}",
            body = "Pengalaman akan dihapus dari profilmu dan tidak lagi terlihat oleh UMKM.",
            confirmLabel = "Ya, Hapus Pengalaman",
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

/** Pemilih bulan-tahun (Figma hanya menampilkan field kalender, tanpa desain picker). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MonthYearPickerDialog(title: String, initial: MonthYear, maxMonth: MonthYear, onPick: (MonthYear) -> Unit, onDismiss: () -> Unit) {
    var year by rememberSaveable { mutableIntStateOf(initial.year) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TemuKaryaColors.BgCanvas,
        title = { Text(title, style = KelolaTypography.SheetTitle, color = TemuKaryaColors.TextHeading) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    YearStep("‹", enabled = year > 1990) { year-- }
                    Text("$year", style = KelolaTypography.SectionTitle, color = TemuKaryaColors.TextHeading, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    YearStep("›", enabled = year < maxMonth.year) { year++ }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 4) {
                    MonthShort.forEachIndexed { index, name ->
                        val candidate = MonthYear(index + 1, year)
                        val enabled = candidate <= maxMonth
                        val selected = candidate == initial
                        Text(
                            name,
                            style = KelolaTypography.Chip,
                            color = when { selected -> TemuKaryaColors.OnAction; enabled -> TemuKaryaColors.TextBody; else -> KelolaColors.Muted.copy(alpha = 0.5f) },
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(56.dp)
                                .clip(CircleShape)
                                .background(if (selected) TemuKaryaColors.ActionPrimary else KelolaColors.Chip)
                                .clickable(enabled = enabled, role = Role.Button) { onPick(candidate) }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", style = KelolaTypography.Link, color = TemuKaryaColors.TextBody) } }
    )
}

@Composable
private fun YearStep(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        symbol,
        style = KelolaTypography.SectionTitle,
        color = if (enabled) TemuKaryaColors.ActionPrimary else KelolaColors.Muted,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(40.dp).clip(CircleShape).clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(vertical = 6.dp)
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ExperienceListScreenPreview() {
    ExperienceListScreen(
        totalYearsLabel = "3+ tahun",
        items = listOf(ExperienceItemUi("1", "Desainer Grafis", "CV Kriya Nusantara, Yogyakarta", "Mar 2022 – Des 2023 · 1 thn 10 bln", "Mendesain katalog produk.")),
        pendingDelete = null,
        onAddClick = {}, onEditClick = {}, onDeleteClick = {}, onConfirmDelete = {}, onDismissDelete = {}, onBack = {}
    )
}
