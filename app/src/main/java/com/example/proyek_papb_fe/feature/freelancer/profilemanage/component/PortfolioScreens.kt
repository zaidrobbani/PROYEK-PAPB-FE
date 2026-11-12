package com.example.proyek_papb_fe.feature.freelancer.profilemanage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.PortfolioCategory
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Satu kartu karya (sudah diformat). */
data class PortfolioCardUi(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val description: String,
    /** Kosong = tidak ada tautan. */
    val link: String,
    @param:DrawableRes val coverRes: Int?,
    val coverUri: String?
)

/** Satu chip filter Kelola Portofolio. */
data class PortfolioFilterUi(val label: String, val isSelected: Boolean)

/**
 * Figma: Kelola Portofolio (535:1081), Kosong (535:1147), dan Hapus Portofolio (Modal) (535:1285).
 * [pendingDelete] tidak null = sheet konfirmasi hapus terbuka.
 */
@Composable
fun PortfolioListScreen(
    totalCount: Int,
    filters: List<PortfolioFilterUi>,
    items: List<PortfolioCardUi>,
    pendingDelete: PortfolioCardUi?,
    onFilterClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalCount == 0) {
        KelolaScaffold(title = "Kelola Portofolio", onBack = onBack, modifier = modifier) {
            KelolaEmptyState(
                iconRes = R.drawable.ic_profile_photo_library,
                title = "Belum ada portofolio",
                body = "Tambahkan karya terbaikmu agar UMKM lebih yakin mengundangmu ke project.",
                actionLabel = "Tambah Portofolio",
                actionIconRes = R.drawable.ic_add,
                onAction = onAddClick
            )
        }
        return
    }
    KelolaScaffold(
        title = "Kelola Portofolio",
        onBack = onBack,
        modifier = modifier,
        bottomBar = { KelolaStickyActionBar("Tambah Portofolio", R.drawable.ic_add, onAddClick) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("$totalCount karya ditampilkan", style = KelolaTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
            Text("Karya ini tampil di profil yang dilihat UMKM.", style = KelolaTypography.Body, color = TemuKaryaColors.TextBody)
        }
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEachIndexed { index, filter -> KelolaFilterChip(filter.label, filter.isSelected) { onFilterClick(index) } }
        }
        if (items.isEmpty()) {
            Text(
                "Belum ada karya di kategori ini.",
                style = KelolaTypography.Body,
                color = TemuKaryaColors.TextBody,
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
            )
        }
        items.forEach { item ->
            PortfolioCard(item, onEdit = { onEditClick(item.id) }, onDelete = { onDeleteClick(item.id) })
        }
    }
    pendingDelete?.let {
        KelolaDeleteSheet(
            title = "Hapus portofolio ini?",
            subtitle = it.title,
            body = "Karya akan dihapus dari profil dan tidak lagi terlihat oleh UMKM. Tindakan ini tidak bisa dibatalkan.",
            confirmLabel = "Ya, Hapus Portofolio",
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

/** Figma "Kelola/Portfolio Card": cover 150, chip kategori, judul, deskripsi, tautan, Edit & Hapus. */
@Composable
private fun PortfolioCard(item: PortfolioCardUi, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(KelolaCardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.dp, KelolaColors.Border, KelolaCardShape)
    ) {
        Cover(item.coverUri, item.coverRes, Modifier.fillMaxWidth().height(150.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                item.categoryLabel,
                style = KelolaTypography.Chip,
                color = KelolaColors.OnSkillChip,
                modifier = Modifier.background(KelolaColors.SkillChip, CircleShape).padding(horizontal = 10.dp, vertical = 3.dp)
            )
            Text(item.title, style = KelolaTypography.CardTitle, color = TemuKaryaColors.TextPrimary)
            Text(item.description, style = KelolaTypography.Body, color = TemuKaryaColors.TextBody)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.link.isNotBlank()) {
                    Icon(painterResource(R.drawable.ic_profile_link), null, tint = KelolaColors.Muted, modifier = Modifier.size(15.dp))
                    Text(
                        item.link,
                        style = KelolaTypography.Caption,
                        color = KelolaColors.Muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                KelolaIconButton(R.drawable.ic_profile_edit, TemuKaryaColors.ActionPrimary, "Edit ${item.title}", onEdit)
                KelolaIconButton(R.drawable.ic_delete, KelolaColors.Error, "Hapus ${item.title}", onDelete)
            }
        }
    }
}

@Composable
private fun Cover(uri: String?, @DrawableRes res: Int?, modifier: Modifier) {
    LocalImage(
        uri = uri,
        fallbackRes = res,
        contentDescription = null,
        modifier = modifier,
        placeholder = {
            Box(modifier.background(KelolaColors.Chip), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_profile_add_photo), null, tint = KelolaColors.Muted, modifier = Modifier.size(32.dp))
            }
        }
    )
}

/** Isian form Tambah/Edit Portofolio. */
data class PortfolioForm(
    val title: String = "",
    val category: PortfolioCategory? = null,
    val description: String = "",
    val link: String = "",
    val skills: List<String> = emptyList(),
    @param:DrawableRes val coverRes: Int? = null,
    val coverUri: String? = null
) {
    val hasCover: Boolean get() = coverRes != null || coverUri != null
}

data class PortfolioFormErrors(val title: String? = null, val category: String? = null, val description: String? = null)

const val PortfolioDescriptionMax = 300

/**
 * Figma: Tambah Portofolio (535:1163) dan Edit Portofolio (535:1222). [isEdit] menampilkan
 * tombol "Hapus Portofolio"; [showDeleteSheet] membuka konfirmasi hapus.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortfolioFormScreen(
    isEdit: Boolean,
    form: PortfolioForm,
    errors: PortfolioFormErrors,
    skillOptions: List<String>,
    showDeleteSheet: Boolean,
    onFormChange: (PortfolioForm) -> Unit,
    onPickCover: () -> Unit,
    onSave: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    KelolaScaffold(
        title = if (isEdit) "Edit Portofolio" else "Tambah Portofolio",
        onBack = onBack,
        modifier = modifier,
        bottomBar = { KelolaStickyActionBar(if (isEdit) "Simpan Perubahan" else "Simpan Portofolio", R.drawable.ic_profile_check, onSave) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            KelolaFieldLabel("Gambar cover")
            if (form.hasCover) {
                Box(modifier = Modifier.fillMaxWidth().height(170.dp).clip(KelolaCardShape)) {
                    Cover(form.coverUri, form.coverRes, Modifier.fillMaxWidth().height(170.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(CircleShape)
                            .background(KelolaColors.InverseSurface.copy(alpha = 0.85f))
                            .clickable(role = Role.Button, onClick = onPickCover)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_profile_camera), null, tint = KelolaColors.InverseOnSurface, modifier = Modifier.size(15.dp))
                        Text("Ganti cover", style = KelolaTypography.Chip, color = KelolaColors.InverseOnSurface)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(KelolaCardShape)
                        .background(TemuKaryaColors.SurfaceWhite)
                        .dashedBorder(KelolaColors.Muted, 16.dp, width = 1.5.dp, dash = 6f, gap = 4f)
                        .clickable(role = Role.Button, onClick = onPickCover),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
                ) {
                    KelolaIconBadge(R.drawable.ic_profile_add_photo, size = 48.dp, iconSize = 24.dp)
                    Text("Unggah gambar cover", style = KelolaTypography.ToggleTitle, color = TemuKaryaColors.TextPrimary)
                    Text("JPG atau PNG, rasio 16:9, maks. 5 MB", style = KelolaTypography.Helper, color = KelolaColors.Muted)
                }
            }
        }
        KelolaTextField(
            label = "Judul karya",
            value = form.title,
            onValueChange = { onFormChange(form.copy(title = it.take(80))) },
            placeholder = "Contoh: Kemasan Pouch Kopi Senja",
            error = errors.title,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
        )
        KelolaDropdownField(
            label = "Kategori",
            options = PortfolioCategory.entries,
            selected = form.category,
            optionLabel = { it.label },
            placeholder = "Pilih kategori",
            onSelect = { onFormChange(form.copy(category = it)) },
            error = errors.category
        )
        KelolaTextField(
            label = "Deskripsi",
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it.take(PortfolioDescriptionMax))) },
            placeholder = "Ceritakan tujuan project, peranmu, dan hasilnya untuk klien.",
            helper = "${form.description.length}/$PortfolioDescriptionMax karakter",
            error = errors.description,
            singleLine = false,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            KelolaFieldLabel("Keahlian terkait")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skillOptions.forEach { skill ->
                    val selected = skill in form.skills
                    KelolaPickChip(skill, selected) {
                        onFormChange(form.copy(skills = if (selected) form.skills - skill else form.skills + skill))
                    }
                }
            }
            KelolaHelper("Pilih dari keahlian di Profil Profesional.", isError = false)
        }
        KelolaTextField(
            label = "Tautan karya (opsional)",
            value = form.link,
            onValueChange = { onFormChange(form.copy(link = it.trim())) },
            placeholder = "https://",
            helper = if (isEdit) null else "Behance, Dribbble, Instagram, atau Google Drive.",
            trailingIconRes = R.drawable.ic_profile_link,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done)
        )
        if (isEdit) {
            KelolaOutlinedButton("Hapus Portofolio", onDeleteClick, iconRes = R.drawable.ic_delete, color = KelolaColors.Error)
        }
    }
    if (showDeleteSheet) {
        KelolaDeleteSheet(
            title = "Hapus portofolio ini?",
            subtitle = form.title,
            body = "Karya akan dihapus dari profil dan tidak lagi terlihat oleh UMKM. Tindakan ini tidak bisa dibatalkan.",
            confirmLabel = "Ya, Hapus Portofolio",
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PortfolioListScreenPreview() {
    PortfolioListScreen(
        totalCount = 1,
        filters = listOf(PortfolioFilterUi("Semua (1)", true), PortfolioFilterUi("Desain Kemasan", false)),
        items = listOf(PortfolioCardUi("1", "Kemasan Pouch Kopi Senja", "Desain Kemasan", "Desain pouch kraft 250 gr.", "behance.net/andiramadhan", R.drawable.img_portfolio_pouch_kopi, null)),
        pendingDelete = null,
        onFilterClick = {}, onAddClick = {}, onEditClick = {}, onDeleteClick = {}, onConfirmDelete = {}, onDismissDelete = {}, onBack = {}
    )
}
