package com.example.proyek_papb_fe.feature.umkm.review.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.feature.umkm.payment.component.FreelancerAvatar
import com.example.proyek_papb_fe.feature.umkm.payment.component.P13
import com.example.proyek_papb_fe.feature.umkm.payment.component.P17
import com.example.proyek_papb_fe.feature.umkm.payment.component.P8
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentCardShape
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentColors
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentInnerShape
import com.example.proyek_papb_fe.feature.umkm.payment.component.PaymentTypography
import com.example.proyek_papb_fe.feature.umkm.payment.component.paymentCardShadow
import kotlinx.coroutines.launch

/** Satu poin uraian revisi yang sedang disusun UMKM. */
data class RevisionPointInput(val title: String = "", val instruction: String = "", val area: String = "")

/** Isi form "Ajukan Catatan & Revisi" (Figma 17:5518). */
data class RevisionFormUi(
    /** "Draf v1.0". */
    val draftLabel: String,
    val projectTitle: String,
    /** "Draf v1.0, Kiriman Andi Ramadhan". */
    val reviewedDraft: String,
    /** "Diterima 24 Okt 2026, 14.15 WIB". */
    val receivedAt: String,
    val aspects: List<String>,
    val selectedAspects: Set<String>,
    val points: List<RevisionPointInput>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewSheet(
    onDismiss: () -> Unit,
    gripColor: Color,
    content: @Composable (hideThen: (() -> Unit) -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isClosing by remember { mutableStateOf(false) }
    val hideThen: (() -> Unit) -> Unit = { action ->
        if (!isClosing) {
            isClosing = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = ReviewSheetShape,
        containerColor = TemuKaryaColors.BgCanvas,
        contentColor = TemuKaryaColors.TextPrimary,
        tonalElevation = 0.dp,
        scrimColor = ReviewColors.Scrim,
        dragHandle = {
            Box(Modifier.padding(top = P13, bottom = P8).size(width = 50.708.dp, height = 6.338.dp).background(gripColor, CircleShape))
        }
    ) {
        content(hideThen)
    }
}

/** Kolom teks polos (BasicTextField) dengan placeholder. */
@Composable
private fun PlainField(value: String, onValueChange: (String) -> Unit, placeholder: String, style: TextStyle, color: Color, modifier: Modifier = Modifier, singleLine: Boolean = false) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = style.copy(color = color),
        singleLine = singleLine,
        cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier,
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) Text(placeholder, style = style, color = ReviewColors.Muted)
                inner()
            }
        }
    )
}

/**
 * Figma: Modal Ajukan Revisi (UMKM) (17:5518): draf yang ditinjau, kategori aspek, poin
 * uraian revisi, "Simpan Draf" / "Kirim Permintaan Revisi". Figma hanya menggambar satu poin;
 * "Tambah Poin" ditambahkan agar UMKM bisa menulis lebih dari satu poin.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RevisionRequestSheet(
    form: RevisionFormUi,
    onToggleAspect: (String) -> Unit,
    onPointChange: (index: Int, RevisionPointInput) -> Unit,
    onAddPoint: () -> Unit,
    onRemovePoint: (index: Int) -> Unit,
    onSaveDraft: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    ReviewSheet(onDismiss = onDismiss, gripColor = ReviewColors.Grip) { hideThen ->
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
            // Header + tombol tutup (17:5533, 17:5555).
            Row(modifier = Modifier.fillMaxWidth().padding(start = P17, end = P17, bottom = P13), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.226.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalArrangement = Arrangement.spacedBy(4.226.dp), itemVerticalAlignment = Alignment.CenterVertically) {
                        Text("Ajukan Catatan & Revisi", style = ReviewTypography.SheetTitle, color = TemuKaryaColors.TextPrimary)
                        Text(
                            form.draftLabel,
                            style = PaymentTypography.Caption,
                            color = PaymentColors.OnGreen,
                            modifier = Modifier.background(PaymentColors.Green, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
                        )
                    }
                    Text(form.projectTitle, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                }
                RoundIconButton(R.drawable.ic_rq_close, 38.031.dp, 12.325.dp, 12.325.dp, PaymentColors.StatusCard, PaymentColors.Heading, "Tutup formulir revisi") { hideThen(onDismiss) }
            }
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = P17, end = 12.677.dp, bottom = P17),
                verticalArrangement = Arrangement.spacedBy(P17)
            ) {
                // 1. Draf yang Ditinjau (17:5559).
                Column(
                    modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(P13),
                    verticalArrangement = Arrangement.spacedBy(P8)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_rq_sync), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 19.368.dp, height = 14.085.dp))
                        Text("Draf yang Ditinjau", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.BgCanvas, PaymentInnerShape).padding(10.564.dp),
                        horizontalArrangement = Arrangement.spacedBy(P8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_rq_draft), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 17.167.dp, height = 14.085.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(form.reviewedDraft, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary)
                            Text(form.receivedAt, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                        }
                    }
                    Row(modifier = Modifier.padding(top = 4.226.dp), horizontalArrangement = Arrangement.spacedBy(P8)) {
                        Icon(painterResource(R.drawable.ic_rq_bulb), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 15.846.dp, height = 17.607.dp))
                        Text("Revisi berbutir memperjelas ekspektasi desain tanpa salah tafsir antar pihak.", style = ReviewTypography.SmallBody, color = TemuKaryaColors.TextBody)
                    }
                }
                // 2. Kategori Aspek Perbaikan (17:5583).
                Column(verticalArrangement = Arrangement.spacedBy(P8)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Kategori Aspek Perbaikan", style = PaymentTypography.Button, color = TemuKaryaColors.TextPrimary)
                        Text("Pilih relevan", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(P8), verticalArrangement = Arrangement.spacedBy(P8)) {
                        form.aspects.forEach { aspect ->
                            val selected = aspect in form.selectedAspects
                            Row(
                                modifier = Modifier
                                    .then(if (selected) Modifier.paymentCardShadow(CircleShape) else Modifier)
                                    .clip(CircleShape)
                                    .background(if (selected) TemuKaryaColors.ActionPrimary else PaymentColors.StatusCard)
                                    .clickable(role = Role.Checkbox) { onToggleAspect(aspect) }
                                    .padding(horizontal = P13, vertical = P8),
                                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selected) Icon(painterResource(R.drawable.ic_rq_chip_check), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(14.085.dp))
                                else Icon(painterResource(R.drawable.ic_rq_plus), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(9.86.dp))
                                Text(aspect, style = PaymentTypography.BodySemi, color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody)
                            }
                        }
                    }
                }
                // 3. Poin Uraian Revisi (17:5610).
                Column(verticalArrangement = Arrangement.spacedBy(P13)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Poin Uraian Revisi", style = PaymentTypography.Button, color = TemuKaryaColors.TextPrimary)
                        Text("${form.points.count { it.title.isNotBlank() && it.instruction.isNotBlank() }} Poin Tercatat", style = PaymentTypography.Caption, color = PaymentColors.Olive)
                    }
                    form.points.forEachIndexed { index, point ->
                        PointCard(index, point, canRemove = form.points.size > 1, onChange = { onPointChange(index, it) }, onRemove = { onRemovePoint(index) })
                    }
                    Row(
                        modifier = Modifier.clip(CircleShape).background(PaymentColors.StatusCard).clickable(role = Role.Button, onClick = onAddPoint).padding(horizontal = P13, vertical = P8),
                        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_rq_plus), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(9.86.dp))
                        Text("Tambah Poin", style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextBody)
                    }
                }
            }
            // 6. Sticky Bottom Action Bar (17:5694).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .dropShadow(RectangleShape, Shadow(radius = P17, color = Color(0x0F684738), offset = DpOffset(0.dp, (-4.226).dp)))
                    .background(ReviewColors.BottomBar)
                    .padding(horizontal = P17, vertical = P13),
                horizontalArrangement = Arrangement.spacedBy(P8)
            ) {
                Row(
                    modifier = Modifier.weight(1f).height(50.708.dp).clip(RoundedCornerShape(12.dp)).background(PaymentColors.StatusCard).clickable(role = Role.Button) { hideThen(onSaveDraft) },
                    horizontalArrangement = Arrangement.spacedBy(6.338.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_rq_save), null, tint = TemuKaryaColors.TextPrimary, modifier = Modifier.size(15.846.dp))
                    Text("Simpan Draf", style = PaymentTypography.Button, color = TemuKaryaColors.TextPrimary)
                }
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.708.dp)
                        .dropShadow(RoundedCornerShape(12.dp), Shadow(radius = 7.395.dp, color = Color(0x40684738), offset = DpOffset(0.dp, 4.226.dp)))
                        .clip(RoundedCornerShape(12.dp))
                        .background(TemuKaryaColors.ActionPrimary)
                        .clickable(role = Role.Button, onClick = onSubmit),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kirim Permintaan Revisi", style = ReviewTypography.ButtonBold, color = TemuKaryaColors.OnAction, maxLines = 1)
                }
            }
        }
    }
}

/** 17:5616: kartu poin (nomor, judul, uraian di kotak putih, area). */
@Composable
private fun PointCard(index: Int, point: RevisionPointInput, canRemove: Boolean, onChange: (RevisionPointInput) -> Unit, onRemove: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, PaymentCardShape).padding(P13),
        verticalArrangement = Arrangement.spacedBy(P8)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(21.128.dp).background(TemuKaryaColors.ActionPrimary, CircleShape), contentAlignment = Alignment.Center) {
                Text("${index + 1}", style = PaymentTypography.Caption, color = TemuKaryaColors.OnAction)
            }
            PlainField(point.title, { onChange(point.copy(title = it.take(60))) }, "Judul poin revisi", PaymentTypography.RowSemi, TemuKaryaColors.TextPrimary, Modifier.weight(1f), singleLine = true)
            Box(
                modifier = Modifier.size(29.579.dp).alpha(if (canRemove) 1f else 0.3f).clip(CircleShape).clickable(enabled = canRemove, role = Role.Button, onClickLabel = "Hapus poin ${index + 1}", onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_rq_delete), "Hapus poin ${index + 1}", tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 12.677.dp, height = 14.262.dp))
            }
        }
        Box(modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp).background(TemuKaryaColors.SurfaceWhite, PaymentInnerShape).padding(10.564.dp)) {
            PlainField(point.instruction, { onChange(point.copy(instruction = it.take(500))) }, "Uraikan perbaikan yang diminta…", ReviewTypography.Instruction, TemuKaryaColors.TextPrimary, Modifier.fillMaxWidth())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_rq_area), null, tint = PaymentColors.Olive, modifier = Modifier.size(14.085.dp))
            Text("Area: ", style = PaymentTypography.Caption, color = PaymentColors.Olive)
            PlainField(point.area, { onChange(point.copy(area = it.take(40))) }, "Bagian desain (opsional)", PaymentTypography.Caption, PaymentColors.Olive, Modifier.weight(1f), singleLine = true)
        }
    }
}

/** Isi sheet "Setujui Draf & Selesaikan Proyek" (Figma 17:6140). */
data class ApproveUi(
    val draftLabel: String,
    val projectTitle: String,
    val freelancerName: String,
    val freelancerFirstName: String,
    val freelancerRole: String,
    val freelancerPhotoUri: String?,
    val files: List<ReviewFileUi>,
    val total: String,
    val feeLabel: String,
    val fee: String,
    val confirmed: Boolean,
    val rating: Int,
    val reviewText: String
)

/**
 * Figma: Konfirmasi Pelepasan Escrow & Selesai Proyek (17:6140) sebagai bottom sheet di atas
 * Review Hasil. Tombol utama aktif setelah kotak "Saya sudah memeriksa…" dicentang (mencegah
 * persetujuan tidak sengaja). Ulasan bintang opsional.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApproveCompletionSheet(
    ui: ApproveUi,
    onConfirmedChange: (Boolean) -> Unit,
    onRate: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit,
    onApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    ReviewSheet(onDismiss = onDismiss, gripColor = ReviewColors.GripApprove) { hideThen ->
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
            // 17:6319: header modal.
            Column(modifier = Modifier.fillMaxWidth().padding(start = P17, end = P17, bottom = P8)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f).padding(top = 2.113.dp), verticalArrangement = Arrangement.spacedBy(3.169.dp)) {
                        Row(
                            modifier = Modifier.background(PaymentColors.Green, CircleShape).padding(horizontal = 10.564.dp, vertical = 2.113.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painterResource(R.drawable.ic_ap_milestone), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(width = 13.557.dp, height = 12.941.dp))
                            Text("${ui.draftLabel}, Final Serah Terima", style = PaymentTypography.Caption, color = PaymentColors.OnGreen)
                        }
                        Text("Setujui Draf & Selesaikan Proyek", style = ReviewTypography.Heading, color = TemuKaryaColors.TextPrimary)
                    }
                    RoundIconButton(R.drawable.ic_ap_close, 33.805.dp, 12.325.dp, 12.325.dp, TemuKaryaColors.SurfaceContainer, PaymentColors.Heading, "Tutup modal") { hideThen(onDismiss) }
                }
                Text(
                    "Setelah disetujui, proyek selesai dan dana diteruskan ke ${ui.freelancerName}.",
                    style = PaymentTypography.Body,
                    color = TemuKaryaColors.TextBody,
                    modifier = Modifier.padding(top = 6.338.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = P17, end = P17, top = P8, bottom = P17),
                verticalArrangement = Arrangement.spacedBy(P13)
            ) {
                // 1. Ringkasan proyek & serah terima (17:6185).
                Column(
                    modifier = Modifier.fillMaxWidth().paymentCardShadow(PaymentCardShape).background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(P13),
                    verticalArrangement = Arrangement.spacedBy(P8)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = P8), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Detail pekerjaan", style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextBody)
                        Text(
                            "${ui.draftLabel} Disetujui",
                            style = PaymentTypography.Caption,
                            color = ReviewColors.DeepBrown,
                            modifier = Modifier.background(PaymentColors.Chip, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
                        )
                    }
                    Text(ui.projectTitle, style = PaymentTypography.Title, color = TemuKaryaColors.TextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.BgCanvas, PaymentCardShape).padding(10.564.dp),
                        horizontalArrangement = Arrangement.spacedBy(P8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FreelancerAvatar(ui.freelancerPhotoUri, 46.482f)
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(ui.freelancerName, style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    "Freelancer",
                                    style = ReviewTypography.Tiny,
                                    color = ReviewColors.OnFreelancerChip,
                                    modifier = Modifier.background(ReviewColors.GreenChip, CircleShape).padding(horizontal = 6.338.dp)
                                )
                            }
                            Text(ui.freelancerRole, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.338.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_ap_folder), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 15.142.dp, height = 11.268.dp))
                            Text("Berkas Master Diserahkan (${ui.files.size} File):", style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
                        }
                        ui.files.forEach { file ->
                            val vector = file.extension in setOf("ai", "eps", "svg")
                            val pdf = file.extension == "pdf"
                            Row(
                                modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, PaymentInnerShape).padding(horizontal = 10.564.dp, vertical = P8),
                                horizontalArrangement = Arrangement.spacedBy(P8),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(
                                        when {
                                            vector -> R.drawable.ic_ap_vector
                                            file.isImage -> R.drawable.ic_rv_image
                                            else -> R.drawable.ic_dn_pdf
                                        }
                                    ),
                                    null,
                                    tint = if (pdf) Color(0xFFBA1A1A) else TemuKaryaColors.TextPrimary,
                                    modifier = Modifier.size(width = if (vector) 16.726.dp else 17.607.dp, height = if (vector) 15.846.dp else 17.607.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(file.name, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(file.meta, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1)
                                }
                                Icon(painterResource(R.drawable.ic_ap_filecheck), null, tint = TemuKaryaColors.TextPrimary, modifier = Modifier.size(width = 24.297.dp, height = 15.846.dp))
                            }
                        }
                    }
                }
                // 2. Rincian pelepasan dana escrow (17:6236).
                Column(
                    modifier = Modifier.fillMaxWidth().paymentCardShadow(PaymentCardShape).background(TemuKaryaColors.SurfaceContainer, PaymentCardShape).padding(P13),
                    verticalArrangement = Arrangement.spacedBy(P8)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_ap_shield), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 14.085.dp, height = 17.607.dp))
                        Text("Dana ditahan TEMUKARYA", style = PaymentTypography.RowSemi, color = PaymentColors.Heading)
                    }
                    Column(modifier = Modifier.padding(vertical = 4.226.dp), verticalArrangement = Arrangement.spacedBy(P8)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Nilai Proyek", style = PaymentTypography.Row, color = TemuKaryaColors.TextBody)
                            Text(ui.total, style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ui.feeLabel, style = PaymentTypography.Row, color = TemuKaryaColors.TextBody)
                            Text(ui.fee, style = PaymentTypography.RowSemi, color = PaymentColors.Olive)
                        }
                    }
                    Row(modifier = Modifier.padding(top = P8), horizontalArrangement = Arrangement.spacedBy(P8)) {
                        Icon(painterResource(R.drawable.ic_ap_wallet), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 15.054.dp, height = 16.374.dp))
                        Text("Diteruskan ke dompet ${ui.freelancerName}. ${ui.feeLabel.substringBefore(" (")} dipotong dari nilai proyek.", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                    }
                }
                // 3. Persetujuan (checkbox) (17:6267).
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .paymentCardShadow(PaymentCardShape)
                        .clip(PaymentCardShape)
                        .background(TemuKaryaColors.SurfaceWhite)
                        .clickable(role = Role.Checkbox) { onConfirmedChange(!ui.confirmed) }
                        .padding(P13),
                    horizontalArrangement = Arrangement.spacedBy(P13)
                ) {
                    val boxShape = RoundedCornerShape(2.641.dp)
                    Box(
                        modifier = Modifier
                            .padding(top = 4.226.dp)
                            .size(21.128.dp)
                            .border(1.5.dp, if (ui.confirmed) TemuKaryaColors.ActionPrimary else ReviewColors.Muted, boxShape)
                            .background(if (ui.confirmed) TemuKaryaColors.ActionPrimary else TemuKaryaColors.SurfaceWhite, boxShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ui.confirmed) Icon(painterResource(R.drawable.ic_profile_check), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        "Saya sudah memeriksa ${ui.draftLabel.lowercase()} dan menyetujuinya sebagai hasil akhir.",
                        style = ReviewTypography.Note,
                        color = TemuKaryaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                // 4. Apresiasi & ulasan (opsional) (17:6278).
                Column(
                    modifier = Modifier.fillMaxWidth().paymentCardShadow(PaymentCardShape).background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(start = P13, end = P13, top = P13, bottom = 19.015.dp),
                    verticalArrangement = Arrangement.spacedBy(P8)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Apresiasi & Ulasan untuk ${ui.freelancerFirstName}", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                        Text(
                            "Opsional",
                            style = PaymentTypography.Caption,
                            color = PaymentColors.Olive,
                            modifier = Modifier.background(PaymentColors.Green, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRow(ui.rating, 24.65.dp, 23.417.dp, 6.338.dp, 3.169.dp, onRate)
                        Text(
                            if (ui.rating > 0) "${ui.rating}.0 (${shortRatingLabel(ui.rating)})" else "Ketuk bintang",
                            style = PaymentTypography.BodySemi,
                            color = TemuKaryaColors.TextPrimary,
                            modifier = Modifier.padding(start = P8)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).background(TemuKaryaColors.BgCanvas, RoundedCornerShape(12.dp)).padding(10.564.dp)) {
                        PlainField(ui.reviewText, { onReviewTextChange(it.take(500)) }, "Tulis kesan singkat untuk ${ui.freelancerFirstName} (opsional)", PaymentTypography.Body, TemuKaryaColors.TextPrimary, Modifier.fillMaxWidth())
                    }
                }
            }
            // 157:21: footer aksi.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .dropShadow(RectangleShape, Shadow(radius = P8, color = Color(0x0F684738), offset = DpOffset(0.dp, (-4.226).dp)))
                    .background(ReviewColors.SheetFooter)
                    .padding(P17),
                verticalArrangement = Arrangement.spacedBy(10.564.dp)
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_ap_lock_small), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 11.268.dp, height = 14.79.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.708.dp)
                        .alpha(if (ui.confirmed) 1f else 0.45f)
                        .clip(RoundedCornerShape(12.677.dp))
                        .background(TemuKaryaColors.ActionPrimary)
                        .clickable(enabled = ui.confirmed, role = Role.Button) { hideThen(onApprove) }
                        .padding(vertical = 14.79.dp),
                    horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_ap_lock_btn), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(width = 14.085.dp, height = 18.487.dp))
                    Text("Setujui & selesaikan proyek", style = PaymentTypography.Button, color = TemuKaryaColors.OnAction)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.708.dp)
                        .clip(RoundedCornerShape(12.677.dp))
                        .background(ReviewColors.Secondary)
                        .clickable(role = Role.Button) { hideThen(onDismiss) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Periksa Kembali Berkas", style = PaymentTypography.Button, color = ReviewColors.OnSecondary, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
