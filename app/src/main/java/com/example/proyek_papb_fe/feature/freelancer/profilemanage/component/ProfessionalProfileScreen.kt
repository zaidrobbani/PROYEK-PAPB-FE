package com.example.proyek_papb_fe.feature.freelancer.profilemanage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Isian form Kelola Profil Profesional (nilai mentah yang sedang diedit). */
data class ProfessionalProfileForm(
    val initials: String,
    val photoUri: String?,
    val fullName: String,
    val headline: String,
    val bio: String,
    val primaryField: String,
    val skills: List<String>,
    /** Tarif dalam digit mentah, mis. "500000"; kosong = belum diisi. */
    val rateDigits: String,
    val city: String,
    val isAvailableForWork: Boolean,
    val acceptsRemoteWork: Boolean
)

/** Pesan validasi per field; null = valid. */
data class ProfessionalProfileErrors(
    val fullName: String? = null,
    val headline: String? = null,
    val bio: String? = null,
    val city: String? = null
) {
    val count: Int get() = listOfNotNull(fullName, headline, bio, city).size
}

/** "500000" → "Rp 500.000 / project" (Figma Field Tarif) tanpa mengubah nilai mentah. */
private object RatePerProjectTransformation : VisualTransformation {
    private const val Prefix = "Rp "
    private const val Suffix = " / proyek"
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        val grouped = StringBuilder()
        val map = IntArray(raw.length + 1)
        raw.forEachIndexed { i, c ->
            if (i > 0 && (raw.length - i) % 3 == 0) grouped.append('.')
            map[i] = Prefix.length + grouped.length
            grouped.append(c)
        }
        map[raw.length] = Prefix.length + grouped.length
        val out = Prefix + grouped + Suffix
        return TransformedText(AnnotatedString(out), object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = map[offset.coerceIn(0, raw.length)]
            override fun transformedToOriginal(offset: Int): Int {
                val inNumber = (offset - Prefix.length).coerceIn(0, grouped.length)
                return grouped.take(inNumber).count { it != '.' }
            }
        })
    }
}

/**
 * Figma: Kelola Profil Profesional (534:961), state Validasi (534:1067) dan Tersimpan (534:1169).
 * Stateless: isian, error, dan snackbar dikelola container.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfessionalProfileScreen(
    form: ProfessionalProfileForm,
    errors: ProfessionalProfileErrors,
    showErrorBanner: Boolean,
    fieldOptions: List<String>,
    showSavedSnackbar: Boolean,
    onFormChange: (ProfessionalProfileForm) -> Unit,
    onPickPhoto: () -> Unit,
    onSave: () -> Unit,
    onViewProfile: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSkill by rememberSaveable { mutableStateOf(false) }

    KelolaScaffold(
        title = "Kelola Profil Profesional",
        onBack = onBack,
        modifier = modifier,
        bottomBar = { KelolaStickyActionBar("Simpan Perubahan", R.drawable.ic_profile_check, onSave) },
        overlay = {
            if (showSavedSnackbar) KelolaSnackbar("Perubahan profil profesional disimpan", "Lihat Profil", onViewProfile)
        }
    ) {
        if (showErrorBanner && errors.count > 0) ErrorBanner(errors.count)
        PhotoCard(initials = form.initials, photoUri = form.photoUri, onPickPhoto = onPickPhoto)

        KelolaSectionTitle("Informasi Dasar", subtitle = "Ditampilkan kepada UMKM di profil dan pengajuanmu.")
        KelolaTextField(
            label = "Nama lengkap",
            value = form.fullName,
            onValueChange = { onFormChange(form.copy(fullName = it)) },
            placeholder = "Nama lengkap",
            error = errors.fullName,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
        )
        KelolaTextField(
            label = "Headline profesional",
            value = form.headline,
            onValueChange = { onFormChange(form.copy(headline = it)) },
            placeholder = "Contoh: Desainer Grafis & Identitas Kemasan",
            helper = "${form.headline.length}/${FreelancerProfile.HEADLINE_MAX} karakter · tampil di bawah namamu",
            error = errors.headline,
            singleLine = false,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        KelolaTextField(
            label = "Bio profesional",
            value = form.bio,
            onValueChange = { onFormChange(form.copy(bio = it)) },
            placeholder = "Ceritakan pengalaman dan keahlianmu.",
            helper = "${form.bio.length}/${FreelancerProfile.BIO_MAX} karakter",
            error = errors.bio,
            singleLine = false,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        KelolaSectionTitle("Keahlian", subtitle = "Dipakai untuk mencocokkan proyek yang relevan.")
        KelolaDropdownField(
            label = "Bidang keahlian utama",
            options = fieldOptions,
            selected = form.primaryField,
            optionLabel = { it },
            placeholder = "Pilih bidang keahlian",
            onSelect = { onFormChange(form.copy(primaryField = it)) }
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            KelolaFieldLabel("Keahlian")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                form.skills.forEach { skill ->
                    KelolaSkillChip(skill, onRemove = { onFormChange(form.copy(skills = form.skills - skill)) })
                }
                KelolaAddChip(
                    "Tambah keahlian",
                    onClick = { showAddSkill = true },
                    enabled = form.skills.size < FreelancerProfile.SKILLS_MAX
                )
            }
            KelolaHelper("${form.skills.size}/${FreelancerProfile.SKILLS_MAX} keahlian", isError = false)
        }

        KelolaSectionTitle("Tarif & Area Layanan")
        KelolaTextField(
            label = "Tarif mulai dari",
            value = form.rateDigits,
            onValueChange = { input -> onFormChange(form.copy(rateDigits = input.filter(Char::isDigit).trimStart('0').take(11))) },
            placeholder = "Rp 0 / proyek",
            helper = "Kisaran awal; nilai akhir tetap mengikuti kesepakatan dengan UMKM.",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            visualTransformation = RatePerProjectTransformation
        )
        KelolaTextField(
            label = "Area layanan",
            value = form.city,
            onValueChange = { onFormChange(form.copy(city = it)) },
            placeholder = "Contoh: Yogyakarta",
            error = errors.city,
            trailingIconRes = R.drawable.ic_profile_location,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done)
        )
        KelolaCard {
            KelolaToggleRow("Siap Terima Proyek", "Tampilkan status ini di profilmu", form.isAvailableForWork) {
                onFormChange(form.copy(isAvailableForWork = it))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(KelolaColors.Border))
            KelolaToggleRow("Terima proyek jarak jauh", "UMKM di luar ${form.city.ifBlank { "kotamu" }} tetap bisa mengundangmu", form.acceptsRemoteWork) {
                onFormChange(form.copy(acceptsRemoteWork = it))
            }
        }
    }

    if (showAddSkill) {
        AddSkillDialog(
            existing = form.skills,
            onAdd = { skill -> onFormChange(form.copy(skills = form.skills + skill)); showAddSkill = false },
            onDismiss = { showAddSkill = false }
        )
    }
}

/** Figma "Banner Validasi": error-container r16, ikon error 20. */
@Composable
private fun ErrorBanner(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().background(KelolaColors.ErrorContainer, KelolaCardShape).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(painterResource(R.drawable.ic_profile_error), null, tint = KelolaColors.OnErrorContainer, modifier = Modifier.size(20.dp))
        Text(
            "Ada $count isian yang perlu diperbaiki sebelum perubahan bisa disimpan.",
            style = KelolaTypography.BodyMedium,
            color = KelolaColors.OnErrorContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Figma "Foto Profil": avatar 72 dengan lencana kamera, keterangan, dan "Ubah foto". */
@Composable
private fun PhotoCard(initials: String, photoUri: String?, onPickPhoto: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, KelolaCardShape)
            .border(1.dp, KelolaColors.Border, KelolaCardShape)
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(72.dp)) {
            Box(modifier = Modifier.size(72.dp).clip(CircleShape).clickable(role = Role.Button, onClickLabel = "Ubah foto", onClick = onPickPhoto)) {
                LocalImage(
                    uri = photoUri,
                    fallbackRes = null,
                    contentDescription = "Foto profil",
                    modifier = Modifier.size(72.dp).clip(CircleShape),
                    placeholder = {
                        Box(Modifier.size(72.dp).background(KelolaColors.Avatar, CircleShape), contentAlignment = Alignment.Center) {
                            Text(initials, style = KelolaTypography.Initials, color = KelolaColors.OnAvatar)
                        }
                    }
                )
            }
            // Figma "Badge Ubah Foto": 26 di (48, 48), garis krem 2.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(26.dp)
                    .background(TemuKaryaColors.BgCanvas, CircleShape)
                    .padding(2.dp)
                    .background(TemuKaryaColors.ActionPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_profile_camera), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(14.dp))
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Foto profil", style = KelolaTypography.ItemTitle, color = TemuKaryaColors.TextPrimary)
            Text("Tampil di kartu Cari Freelancer. JPG atau PNG, maks. 2 MB.", style = KelolaTypography.Helper, color = TemuKaryaColors.TextBody)
            Row(
                modifier = Modifier.padding(top = 4.dp).clip(RoundedCornerShape(6.dp)).clickable(role = Role.Button, onClick = onPickPhoto),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_profile_upload), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(16.dp))
                Text("Ubah foto", style = KelolaTypography.Link, color = TemuKaryaColors.ActionPrimary)
            }
        }
    }
}

/** Dialog sederhana untuk "+ Tambah keahlian" (Figma tidak mendefinisikan layar khusus). */
@Composable
private fun AddSkillDialog(existing: List<String>, onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val trimmed = text.trim()
    val duplicate = existing.any { it.equals(trimmed, ignoreCase = true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TemuKaryaColors.BgCanvas,
        title = { Text("Tambah keahlian", style = KelolaTypography.SheetTitle, color = TemuKaryaColors.TextHeading) },
        text = {
            KelolaTextField(
                label = "Nama keahlian",
                value = text,
                onValueChange = { text = it.take(40) },
                placeholder = "Contoh: Ilustrasi Digital",
                error = if (duplicate) "Keahlian ini sudah ada." else null,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done)
            )
        },
        confirmButton = {
            TextButton(onClick = { onAdd(trimmed) }, enabled = trimmed.isNotEmpty() && !duplicate) {
                Text("Tambah", style = KelolaTypography.Link, color = TemuKaryaColors.ActionPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", style = KelolaTypography.Link, color = TemuKaryaColors.TextBody) }
        }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ProfessionalProfileScreenPreview() {
    ProfessionalProfileScreen(
        form = ProfessionalProfileForm(
            initials = "AR", photoUri = null, fullName = "Andi Ramadhan", headline = "Desainer Grafis & Identitas Kemasan",
            bio = "Desainer grafis berbasis di Yogyakarta.", primaryField = "Desain Grafis & Branding",
            skills = listOf("Desain Kemasan Pouch", "Logo Branding"), rateDigits = "500000", city = "Yogyakarta",
            isAvailableForWork = true, acceptsRemoteWork = true
        ),
        errors = ProfessionalProfileErrors(),
        showErrorBanner = false,
        fieldOptions = listOf("Desain Grafis & Branding"),
        showSavedSnackbar = false,
        onFormChange = {}, onPickPhoto = {}, onSave = {}, onViewProfile = {}, onBack = {}
    )
}
