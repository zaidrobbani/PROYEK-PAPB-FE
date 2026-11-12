package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography
import kotlinx.coroutines.launch

/** Jenis file untuk ikon & warna kotak ikon di daftar file terlampir. */
enum class DraftFileKind { PDF, VECTOR, IMAGE, ARCHIVE }

/** Satu file terlampir (sudah diformat). */
data class DraftFileUi(
    val id: String,
    val name: String,
    /** "12.4 MB"; null bila ukuran tidak diketahui. */
    val sizeLabel: String?,
    /** Label format, mis. "PDF", "Adobe Illustrator". */
    val typeLabel: String,
    val kind: DraftFileKind
)

/** Data tampilan modal Unggah Draf Pekerjaan (sudah diformat). */
data class UploadDraftUiState(
    val projectTitle: String,
    /** Nama depan kontak UMKM, untuk label catatan review. */
    val contactFirstName: String,
    /** Nomor draf: 1 = "Draf v1.0 (Awal)". */
    val version: Int,
    val files: List<DraftFileUi>,
    val note: String
) {
    val versionLabel: String get() = "Draf v$version.0 " + if (version == 1) "(Awal)" else "(Revisi ${version - 1})"
    val canSend: Boolean get() = files.isNotEmpty()
}

private object UploadDraftColors {
    val Scrim = Color(0x99000000) // rgba(0,0,0,0.6)
    val Handle = Color(0xFFD8C2B8)
    val CardBorder = Color(0x80E2D5BA) // 0.5
    val FileBorder = Color(0x99E2D5BA) // 0.6
    val NoteBorder = Color(0xB3E2D5BA) // 0.7
    val DropBorder = Color(0x4D684738) // 0.3
    val VersionPill = Color(0x1A684738) // 0.1
    val VersionPillBorder = Color(0x334E3123) // 0.2
    val InfoBackground = Color(0x80DBE3C4) // 0.5
    val InfoBorder = Color(0x335A6149) // 0.2
    val PdfBackground = Color(0xFFFFDAD6)
    val VectorBackground = Color(0xFFDEE6C7)
    val VectorIcon = Color(0xFF424A33)
    val Shadow = Color(0x0D000000) // rgba(0,0,0,0.05)
}

/** Ukuran teks Figma 17:5174 yang belum ada di tema bersama. */
private object UploadDraftTypography {
    private fun style(weight: FontWeight, size: Float, lineHeight: Float) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp
    )

    val Title = style(FontWeight.SemiBold, 19.015f, 25.354f)
    val Chip = style(FontWeight.Normal, 11.621f, 17.431f)
    val Hint = style(FontWeight.Normal, 12.677f, 19.015f)
    val FileName = style(FontWeight.SemiBold, 14.79f, 21.128f)
    val FileMeta = style(FontWeight.Normal, 11.621f, 17.431f)
    val Tag = style(FontWeight.Medium, 10.564f, 15.846f)
    val Note = style(FontWeight.Normal, 12.677f, 20.6f)
    val Info = style(FontWeight.SemiBold, 12.677f, 20.6f)
    val ChecklistTitle = style(FontWeight.SemiBold, 11.621f, 17.431f)
}

private val SheetShape = RoundedCornerShape(topStart = 25.354.dp, topEnd = 25.354.dp)
private val CardShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(12.dp)
private val SheetPadding = 21.13.dp

/**
 * Figma: Modal Unggah Draf (Freelancer) (17:5174) — bottom sheet di atas Ruang Chat,
 * dibuka dari "+" → "Unggah Draf Pekerjaan" (hanya untuk project berstatus Project Berjalan).
 * Stateless: file, catatan, dan aksi dikelola container. Header & tombol bawah tetap,
 * isi di tengah di-scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDraftSheet(
    state: UploadDraftUiState,
    onPickFiles: () -> Unit,
    onRemoveFile: (id: String) -> Unit,
    onNoteChange: (String) -> Unit,
    /** "Batal / Simpan Draf": tutup modal, file & catatan tetap tersimpan. */
    onSaveAndClose: () -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isClosing by remember { mutableStateOf(false) }
    // Tutup dengan animasi dulu, baru jalankan aksi (pola sama dengan sheet Batalkan Project).
    val hideThen: (() -> Unit) -> Unit = { action ->
        if (!isClosing) {
            isClosing = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = TemuKaryaColors.BgCanvas,
        contentColor = TemuKaryaColors.TextPrimary,
        tonalElevation = 0.dp,
        scrimColor = UploadDraftColors.Scrim,
        dragHandle = {
            // Figma: handle 50.7×6.3 di top 21.1, header di top 48.6.
            Box(
                modifier = Modifier
                    .padding(top = 21.13.dp, bottom = 21.13.dp)
                    .size(width = 50.708.dp, height = 6.338.dp)
                    .background(UploadDraftColors.Handle, CircleShape)
            )
        }
    ) {
        // Figma: tinggi maks. sheet 725 dari layar 915.
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = 725.dp)) {
            SheetHeader(
                projectTitle = state.projectTitle,
                milestone = state.version,
                onClose = { hideThen(onDismiss) }
            )
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(start = SheetPadding, end = SheetPadding, top = 16.903.dp, bottom = 16.903.dp),
                verticalArrangement = Arrangement.spacedBy(16.903.dp)
            ) {
                VersionCard(version = state.version, versionLabel = state.versionLabel)
                DropZone(onPickFiles = onPickFiles)
                if (state.files.isNotEmpty()) {
                    AttachedFiles(files = state.files, onRemoveFile = onRemoveFile)
                }
                ReviewNote(contactFirstName = state.contactFirstName, note = state.note, onNoteChange = onNoteChange)
                PreSendChecklist()
                InfoBanner()
            }
            SheetFooter(
                canSend = state.canSend,
                onSaveAndClose = { hideThen(onSaveAndClose) },
                onSend = { hideThen(onSend) }
            )
        }
    }
}

@Composable
private fun SheetHeader(projectTitle: String, milestone: Int, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SheetPadding)
            .drawBehind {
                val y = size.height - 0.528.dp.toPx()
                drawLine(TemuKaryaColors.SurfaceContainer, Offset(0f, y), Offset(size.width, y), 1.056.dp.toPx())
            }
            .padding(bottom = 13.733.dp),
        horizontalArrangement = Arrangement.spacedBy(12.677.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.113.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Unggah Draf Pekerjaan",
                    style = UploadDraftTypography.Title,
                    color = TemuKaryaColors.TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "Milestone $milestone",
                    style = UploadDraftTypography.Chip,
                    color = TemuKaryaColors.OnChipGreen,
                    maxLines = 1,
                    modifier = Modifier
                        .background(TemuKaryaColors.ChipGreen, CircleShape)
                        .padding(horizontal = 8.451.dp, vertical = 2.113.dp)
                )
            }
            Text(
                text = projectTitle,
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextBody,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(33.805.dp)
                .clip(CircleShape)
                .background(TemuKaryaColors.SurfaceContainer)
                .clickable(role = Role.Button, onClickLabel = "Tutup", onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(R.drawable.ic_draft_close), contentDescription = "Tutup")
        }
    }
}

@Composable
private fun VersionCard(version: Int, versionLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.056.dp, UploadDraftColors.CardBorder, CardShape)
            .padding(horizontal = 15.846.dp, vertical = 11.62.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(R.drawable.ic_draft_layers), contentDescription = null)
        Column(modifier = Modifier.weight(1f)) {
            Text("Versi Draf Pekerjaan", style = TemuKaryaTypography.LabelSemiBold, color = TemuKaryaColors.TextPrimary)
            Text(
                text = "Sesuai kesepakatan tenggat Draf $version",
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextBody
            )
        }
        Text(
            text = versionLabel,
            style = TemuKaryaTypography.CaptionSemiBold,
            color = TemuKaryaColors.TextHeading,
            maxLines = 1,
            modifier = Modifier
                .clip(CircleShape)
                .background(UploadDraftColors.VersionPill)
                .border(1.056.dp, UploadDraftColors.VersionPillBorder, CircleShape)
                .padding(horizontal = 13.733.dp, vertical = 5.282.dp)
        )
    }
}

@Composable
private fun DropZone(onPickFiles: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .drawBehind {
                val stroke = 2.113.dp.toPx()
                drawRoundRect(
                    color = UploadDraftColors.DropBorder,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = size.copy(width = size.width - stroke, height = size.height - stroke),
                    cornerRadius = CornerRadius(16.dp.toPx() - stroke / 2),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
                    )
                )
            }
            .clickable(role = Role.Button, onClickLabel = "Pilih file", onClick = onPickFiles)
            .padding(19.016.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Box(
            modifier = Modifier
                .softShadow(CircleShape)
                .size(46.482.dp)
                .background(TemuKaryaColors.SurfaceContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(R.drawable.ic_draft_upload), contentDescription = null)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.113.dp)) {
            Text(
                text = "Tarik & Lepaskan File Desain ke Sini",
                style = TemuKaryaTypography.LabelSemiBold,
                color = TemuKaryaColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Mendukung AI, PSD, PDF, ZIP, PNG (Maks. 50 MB)",
                style = UploadDraftTypography.Hint,
                color = TemuKaryaColors.TextBody,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 4.226.dp)
                .softShadow(RoundedCornerShape(8.451.dp))
                .clip(RoundedCornerShape(8.451.dp))
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(role = Role.Button, onClick = onPickFiles)
                .padding(horizontal = 16.903.dp, vertical = 6.338.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.ic_draft_add), contentDescription = null)
            Text("Pilih File dari Perangkat", style = TemuKaryaTypography.CaptionMedium, color = TemuKaryaColors.OnAction)
        }
    }
}

@Composable
private fun AttachedFiles(files: List<DraftFileUi>, onRemoveFile: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
        Text(
            text = "File Terlampir (${files.size} File Siap Serah Terima):",
            style = TemuKaryaTypography.CaptionSemiBold,
            color = TemuKaryaColors.TextPrimary
        )
        files.forEach { file -> AttachedFileRow(file = file, onRemove = { onRemoveFile(file.id) }) }
    }
}

@Composable
private fun AttachedFileRow(file: DraftFileUi, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(CardShape)
            .clip(CardShape)
            .background(TemuKaryaColors.SurfaceWhite)
            .border(1.056.dp, UploadDraftColors.FileBorder, CardShape)
            .padding(11.62.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.564.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FileKindIcon(kind = file.kind)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.113.dp)) {
                Text(
                    text = file.name,
                    style = UploadDraftTypography.FileName,
                    color = TemuKaryaColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp), verticalAlignment = Alignment.CenterVertically) {
                    file.sizeLabel?.let {
                        Text(it, style = UploadDraftTypography.FileMeta, color = TemuKaryaColors.TextBody, maxLines = 1)
                    }
                    Text(
                        text = file.typeLabel,
                        style = UploadDraftTypography.Tag,
                        color = TemuKaryaColors.OnChipGreen,
                        maxLines = 1,
                        modifier = Modifier
                            .background(TemuKaryaColors.ChipGreen, RoundedCornerShape(4.226.dp))
                            .padding(horizontal = 6.338.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(start = 8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Centang = file lolos cek format & ukuran (AI/PSD/PDF/ZIP/PNG, maks. 50 MB).
            Image(painterResource(R.drawable.ic_draft_check), contentDescription = "File valid")
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClickLabel = "Hapus ${file.name}", onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.ic_draft_delete), contentDescription = "Hapus file")
            }
        }
    }
}

@Composable
private fun FileKindIcon(kind: DraftFileKind) {
    val (background, iconRes, tint) = when (kind) {
        DraftFileKind.PDF -> Triple(UploadDraftColors.PdfBackground, R.drawable.ic_draft_pdf, null)
        DraftFileKind.VECTOR -> Triple(UploadDraftColors.VectorBackground, R.drawable.ic_draft_vector, null)
        DraftFileKind.IMAGE -> Triple(UploadDraftColors.VectorBackground, R.drawable.ic_image, UploadDraftColors.VectorIcon)
        DraftFileKind.ARCHIVE -> Triple(TemuKaryaColors.SurfaceContainer, R.drawable.ic_document, TemuKaryaColors.TextHeading)
    }
    Box(
        modifier = Modifier
            .size(38.031.dp)
            .background(background, RoundedCornerShape(8.451.dp)),
        contentAlignment = Alignment.Center
    ) {
        KindIcon(iconRes = iconRes, tint = tint)
    }
}

@Composable
private fun KindIcon(@DrawableRes iconRes: Int, tint: Color?) {
    if (tint == null) {
        Image(painterResource(iconRes), contentDescription = null)
    } else {
        androidx.compose.material3.Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.607.dp)
        )
    }
}

@Composable
private fun ReviewNote(contactFirstName: String, note: String, onNoteChange: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(bottom = 5.282.dp),
        verticalArrangement = Arrangement.spacedBy(6.338.dp)
    ) {
        Text(
            text = "Catatan & Panduan Review untuk $contactFirstName:",
            style = TemuKaryaTypography.CaptionSemiBold,
            color = TemuKaryaColors.TextPrimary
        )
        BasicTextField(
            value = note,
            onValueChange = onNoteChange,
            textStyle = UploadDraftTypography.Note.copy(color = TemuKaryaColors.TextPrimary),
            cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.323.dp)
                        .clip(CardShape)
                        .background(TemuKaryaColors.SurfaceWhite)
                        .border(1.056.dp, UploadDraftColors.NoteBorder, CardShape)
                        .padding(13.733.dp)
                ) {
                    if (note.isEmpty()) {
                        Text(
                            text = "Tulis catatan untuk klien: bagian yang perlu ditinjau, opsi yang disiapkan, dll.",
                            style = UploadDraftTypography.Note,
                            color = TemuKaryaColors.TextPlaceholder
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun PreSendChecklist() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.056.dp, UploadDraftColors.CardBorder, CardShape)
            .padding(start = 13.733.dp, end = 13.733.dp, top = 12.677.dp, bottom = 13.733.dp),
        verticalArrangement = Arrangement.spacedBy(6.338.dp)
    ) {
        Text("Cek sebelum kirim", style = UploadDraftTypography.ChecklistTitle, color = TemuKaryaColors.TextBody)
        Column(verticalArrangement = Arrangement.spacedBy(4.226.dp)) {
            ChecklistItem("Resolusi gambar minimum 300 DPI siap cetak")
            ChecklistItem("Font telah di-convert to outlines (vector path)")
        }
    }
}

@Composable
private fun ChecklistItem(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.ic_draft_checklist), contentDescription = null)
        Text(text, style = UploadDraftTypography.Hint, color = TemuKaryaColors.TextPrimary)
    }
}

@Composable
private fun InfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(UploadDraftColors.InfoBackground)
            .border(1.056.dp, UploadDraftColors.InfoBorder, CardShape)
            .padding(13.733.dp),
        horizontalArrangement = Arrangement.spacedBy(10.564.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(painterResource(R.drawable.ic_draft_verified), contentDescription = null)
        Text(
            text = "Draf dikirim ke klien untuk ditinjau.",
            style = UploadDraftTypography.Info,
            color = TemuKaryaColors.OnChipGreen
        )
    }
}

@Composable
private fun SheetFooter(canSend: Boolean, onSaveAndClose: () -> Unit, onSend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SheetPadding)
            .drawBehind {
                drawLine(TemuKaryaColors.SurfaceContainer, Offset(0f, 0f), Offset(size.width, 0f), 1.056.dp.toPx())
            }
            .padding(top = 9.507.dp, bottom = 12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        FooterButton(
            text = "Batal / Simpan Draf",
            background = TemuKaryaColors.SurfaceContainer,
            content = TemuKaryaColors.TextPrimary,
            onClick = onSaveAndClose,
            modifier = Modifier.weight(1f)
        )
        FooterButton(
            text = "Kirim Draf ke Klien",
            background = TemuKaryaColors.ActionPrimary,
            content = TemuKaryaColors.OnAction,
            iconRes = R.drawable.ic_draft_send,
            enabled = canSend,
            elevation = 4.226.dp,
            onClick = onSend,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FooterButton(
    text: String,
    background: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    enabled: Boolean = true,
    elevation: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .then(
                if (elevation > 0.dp && enabled) {
                    Modifier.dropShadow(
                        shape = ButtonShape,
                        shadow = Shadow(radius = 6.338.dp, color = TemuKaryaColors.ShadowButton, offset = DpOffset(0.dp, elevation))
                    )
                } else Modifier
            )
            .clip(ButtonShape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.903.dp, vertical = 12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let { Image(painterResource(it), contentDescription = null) }
        Text(text, style = TemuKaryaTypography.LabelSemiBold, color = content, maxLines = 1, textAlign = TextAlign.Center)
    }
}

/** drop-shadow(0 1.056 1.056 rgba(0,0,0,0.05)) dari Figma. */
private fun Modifier.softShadow(shape: androidx.compose.ui.graphics.Shape): Modifier =
    dropShadow(shape = shape, shadow = Shadow(radius = 1.056.dp, color = UploadDraftColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun UploadDraftSheetPreview() {
    UploadDraftSheet(
        state = UploadDraftUiState(
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            contactFirstName = "Rina",
            version = 1,
            files = listOf(
                DraftFileUi("1", "KopiSenja_Mockup_Pouch_v1.pdf", "12.4 MB", "PDF", DraftFileKind.PDF),
                DraftFileUi("2", "Logo_Vector_KedaiKopiSenja.ai", "8.1 MB", "Adobe Illustrator", DraftFileKind.VECTOR)
            ),
            note = ""
        ),
        onPickFiles = {}, onRemoveFile = {}, onNoteChange = {}, onSaveAndClose = {}, onSend = {}, onDismiss = {}
    )
}
