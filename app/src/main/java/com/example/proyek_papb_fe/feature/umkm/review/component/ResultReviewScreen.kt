package com.example.proyek_papb_fe.feature.umkm.review.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.LocalImage
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

/** Satu berkas yang dikirim freelancer (sudah diformat). */
data class ReviewFileUi(
    val name: String,
    /** "18.4 MB, Berkas PDF". */
    val meta: String,
    val extension: String,
    val uri: String,
    val isImage: Boolean
)

/** Data tampilan Review Hasil Project (sudah diformat, dari draf terkirim project ini). */
data class ResultReviewUiState(
    val projectTitle: String,
    val freelancerName: String,
    val freelancerPhotoUri: String?,
    val freelancerRating: String,
    val projectValue: String,
    /** Catatan freelancer saat mengirim draf; kosong = tidak ada catatan. */
    val note: String,
    /** "Draf Pengiriman v2.0". */
    val draftLabel: String,
    /** "Hari ini, 10.45 WIB". */
    val sentAt: String,
    val files: List<ReviewFileUi>
) {
    val previewImage: ReviewFileUi? get() = files.firstOrNull { it.isImage }
}

/**
 * Figma: Review Hasil Project (17:1468), judul "Review Hasil". UMKM meninjau draf terbaru yang
 * dikirim freelancer lalu memilih "Minta Revisi" (17:5518) atau "Setujui & Selesai" (17:6140).
 * Isi di-scroll; Sticky Bottom Actions Bar tetap di bawah.
 */
@Composable
fun ResultReviewScreen(
    state: ResultReviewUiState,
    onBack: () -> Unit,
    onChatClick: () -> Unit,
    onZoomClick: () -> Unit,
    onDownloadFile: (ReviewFileUi) -> Unit,
    onRequestRevision: () -> Unit,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Review Hasil", onBack = onBack)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = P17, end = P17, top = 3.61.dp, bottom = 152.dp)
            ) {
                StatusChips()
                ContextCard(state, onChatClick, Modifier.padding(top = P17))
                NoteCard(state, Modifier.padding(top = P17))
                PreviewCard(state, onZoomClick, onDownloadFile, Modifier.padding(top = P17))
                FilesCard(state.files, onDownloadFile, Modifier.padding(top = P17))
            }
            ActionBar(onRequestRevision, onApprove, Modifier.align(Alignment.BottomCenter))
        }
    }
}

/** 17:1472: "Dana ditahan" dan "Menunggu Review". */
@Composable
private fun StatusChips() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = P8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.background(ReviewColors.GreenChip, CircleShape).padding(horizontal = P13, vertical = 4.226.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_rv_held_check), null, tint = ReviewColors.OnGreenChip, modifier = Modifier.size(width = 14.526.dp, height = 13.865.dp))
            Text("Dana ditahan", style = PaymentTypography.Caption, color = ReviewColors.OnGreenChip)
        }
        Row(
            modifier = Modifier
                .paymentCardShadow(CircleShape)
                .background(ReviewColors.PendingChip, CircleShape)
                .padding(horizontal = P13, vertical = 4.226.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(P8).background(ReviewColors.PendingDot, CircleShape))
            Text("Menunggu Review", style = PaymentTypography.Caption, color = ReviewColors.OnPending)
        }
    }
}

/** 17:1494: judul project, freelancer, nilai proyek di rekening TEMUKARYA. */
@Composable
private fun ContextCard(state: ResultReviewUiState, onChatClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceWhite, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P17)
    ) {
        Text(state.projectTitle, style = ReviewTypography.Heading, color = PaymentColors.Heading, modifier = Modifier.padding(top = 4.754.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, PaymentInnerShape).padding(P13),
            horizontalArrangement = Arrangement.spacedBy(P13),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FreelancerAvatar(state.freelancerPhotoUri, 46.482f)
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.freelancerName, style = PaymentTypography.RowSemi, color = PaymentColors.Heading, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Icon(painterResource(R.drawable.ic_rv_verified), null, tint = PaymentColors.Olive, modifier = Modifier.size(width = 11.268.dp, height = 14.085.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_payment_star), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 12.325.dp, height = 11.709.dp))
                        Text(state.freelancerRating, style = PaymentTypography.Body, color = PaymentColors.Heading)
                    }
                    Text(
                        "Freelancer Mitra",
                        style = PaymentTypography.Caption,
                        color = ReviewColors.OnGreenChip,
                        modifier = Modifier.background(ReviewColors.GreenChip, RoundedCornerShape(4.226.dp)).padding(horizontal = 6.338.dp)
                    )
                }
            }
            RoundIconButton(R.drawable.ic_rv_chat, 38.031.dp, 15.846.dp, 15.846.dp, TemuKaryaColors.SurfaceContainer, PaymentColors.Heading, "Chat freelancer", onChatClick)
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, PaymentInnerShape).padding(P13),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_rv_lock), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 15.494.dp, height = 20.336.dp))
                Column {
                    Text("Nilai proyek", style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
                    Text(state.projectValue, style = PaymentTypography.Title, color = PaymentColors.Heading)
                }
            }
            Text(
                "Rekening TEMUKARYA",
                style = PaymentTypography.Caption,
                color = PaymentColors.Olive,
                modifier = Modifier
                    .paymentCardShadow(CircleShape)
                    .background(TemuKaryaColors.SurfaceWhite, CircleShape)
                    .padding(horizontal = 10.564.dp, vertical = 4.226.dp)
            )
        }
    }
}

/** 17:1532: "Catatan dari {freelancer}" berisi catatan kiriman draf. */
@Composable
private fun NoteCard(state: ResultReviewUiState, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P8)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_rv_notes), null, tint = PaymentColors.Heading, modifier = Modifier.size(17.607.dp))
            Text("Catatan dari ${state.freelancerName}", style = PaymentTypography.RowSemi, color = PaymentColors.Heading)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TemuKaryaColors.SurfaceWhite, PaymentInnerShape)
                .padding(start = 14.79.dp, end = 14.79.dp, top = 13.997.dp, bottom = 14.79.dp),
            verticalArrangement = Arrangement.spacedBy(10.564.dp)
        ) {
            Text(
                if (state.note.isBlank()) "Freelancer tidak menambahkan catatan untuk draf ini." else "“${state.note}”",
                style = ReviewTypography.Quote,
                color = TemuKaryaColors.TextBody
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(state.draftLabel, style = PaymentTypography.Caption, color = ReviewColors.Muted)
                Text(state.sentAt, style = PaymentTypography.Body, color = ReviewColors.Muted)
            }
        }
    }
}

/** 17:1547: pratinjau hasil (gambar pertama bila ada), spesifikasi berkas, aksi cepat. */
@Composable
private fun PreviewCard(state: ResultReviewUiState, onZoomClick: () -> Unit, onDownloadFile: (ReviewFileUi) -> Unit, modifier: Modifier) {
    val main = state.previewImage ?: state.files.firstOrNull()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceWhite, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_rv_image), null, tint = PaymentColors.Heading, modifier = Modifier.size(15.846.dp))
                Text("Pratinjau Hasil Desain", style = PaymentTypography.RowSemi, color = PaymentColors.Heading)
            }
            Text(
                "${state.files.size} berkas",
                style = PaymentTypography.Caption,
                color = PaymentColors.Olive,
                modifier = Modifier.background(ReviewColors.GreenChip, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(PaymentCardShape).background(TemuKaryaColors.SurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            val image = state.previewImage
            if (image != null) {
                LocalImage(uri = image.uri, fallbackRes = null, contentDescription = "Pratinjau ${image.name}", modifier = Modifier.fillMaxSize(), placeholder = { PreviewPlaceholder(image) })
            } else if (main != null) {
                PreviewPlaceholder(main)
            }
        }
        if (main != null) {
            Row(
                modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, PaymentInnerShape).padding(10.564.dp),
                horizontalArrangement = Arrangement.spacedBy(P8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(main.name, style = PaymentTypography.BodySemi, color = PaymentColors.Heading, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(main.meta, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1)
                }
                Icon(painterResource(R.drawable.ic_rv_filespec), null, tint = PaymentColors.Heading, modifier = Modifier.size(17.607.dp))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.226.dp), horizontalArrangement = Arrangement.spacedBy(P8)) {
                QuickAction(R.drawable.ic_rv_zoom, 14.262f, "Perbesar Tampilan", Modifier.weight(1f), onZoomClick)
                QuickAction(R.drawable.ic_rv_download, 12.677f, "Unduh Mockup", Modifier.weight(1f)) { onDownloadFile(main) }
            }
        }
    }
}

@Composable
private fun PreviewPlaceholder(file: ReviewFileUi) {
    val visual = fileVisual(file.extension, 0)
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(P8)) {
        Box(Modifier.size(64.dp).background(visual.background, PaymentInnerShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(visual.iconRes), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = visual.iconWidth * 1.4f, height = visual.iconHeight * 1.4f))
        }
        Text(file.name, style = PaymentTypography.BodySemi, color = PaymentColors.Heading, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = P17))
    }
}

@Composable
private fun QuickAction(iconRes: Int, iconSize: Float, label: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(42.256.dp)
            .clip(PaymentInnerShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(iconRes), null, tint = PaymentColors.Heading, modifier = Modifier.size(iconSize.dp))
        Text(label, style = PaymentTypography.BodySemi, color = PaymentColors.Heading, textAlign = TextAlign.Center, maxLines = 1)
    }
}

/** 17:1576: "Daftar Berkas Siap Unduh (n)". */
@Composable
private fun FilesCard(files: List<ReviewFileUi>, onDownloadFile: (ReviewFileUi) -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceWhite, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_rv_folder), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 17.607.dp, height = 14.085.dp))
            Text("Daftar Berkas Siap Unduh (${files.size})", style = PaymentTypography.RowSemi, color = PaymentColors.Heading, modifier = Modifier.weight(1f))
            Text("Semua Format Lengkap", style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
        }
        Column(verticalArrangement = Arrangement.spacedBy(P8)) {
            files.forEachIndexed { index, file ->
                val visual = fileVisual(file.extension, index)
                Row(
                    modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, PaymentInnerShape).padding(P13),
                    horizontalArrangement = Arrangement.spacedBy(P13),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(42.256.dp).background(visual.background, PaymentInnerShape), contentAlignment = Alignment.Center) {
                        Icon(painterResource(visual.iconRes), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = visual.iconWidth, height = visual.iconHeight))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(file.name, style = PaymentTypography.RowSemi, color = PaymentColors.Heading, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(file.meta, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                    }
                    RoundIconButton(R.drawable.ic_rv_download, 33.805.dp, 12.677.dp, 12.677.dp, TemuKaryaColors.SurfaceContainer, PaymentColors.Heading, "Unduh ${file.name}") { onDownloadFile(file) }
                }
            }
        }
    }
}

/** 17:1642: "Minta Revisi" (1 bagian) dan "Setujui & Selesai" (1.4 bagian) + keterangan. */
@Composable
private fun ActionBar(onRequestRevision: () -> Unit, onApprove: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(RectangleShape, Shadow(radius = P17, color = ReviewColors.ShadowWarm, offset = DpOffset(0.dp, (-4.226).dp)))
            .background(ReviewColors.BottomBar)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P8)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(P13)) {
            ActionButton(R.drawable.ic_rv_revise, 15.846f, 14.085f, "Minta Revisi", TemuKaryaColors.SurfaceContainer, PaymentColors.Heading, Modifier.weight(1f), onRequestRevision)
            ActionButton(R.drawable.ic_rv_approve, 17.607f, 17.607f, "Setujui & Selesai", TemuKaryaColors.ActionPrimary, TemuKaryaColors.OnAction, Modifier.weight(1.4f), onApprove)
        }
        Text(
            "Dengan menyetujui, Anda mengonfirmasi pekerjaan telah sesuai kesepakatan.",
            style = PaymentTypography.Caption,
            color = PaymentColors.NetText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = P8)
        )
    }
}

@Composable
private fun ActionButton(iconRes: Int, w: Float, h: Float, label: String, background: androidx.compose.ui.graphics.Color, content: androidx.compose.ui.graphics.Color, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(50.708.dp)
            .paymentCardShadow(RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(iconRes), null, tint = content, modifier = Modifier.size(width = w.dp, height = h.dp))
        Text(label, style = PaymentTypography.Button, color = content, maxLines = 1)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ResultReviewScreenPreview() {
    ResultReviewScreen(
        state = ResultReviewUiState(
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi", freelancerName = "Andi Ramadhan", freelancerPhotoUri = null,
            freelancerRating = "4.9", projectValue = "Rp 1.500.000", note = "Halo Bu Rina! Berikut draf mockup kemasan.",
            draftLabel = "Draf Pengiriman v2.0", sentAt = "Hari ini, 10.45 WIB",
            files = listOf(ReviewFileUi("Draf_Kemasan_Kopi_Senja_Final_v2.pdf", "18.4 MB, Berkas PDF", "pdf", "", false))
        ),
        onBack = {}, onChatClick = {}, onZoomClick = {}, onDownloadFile = {}, onRequestRevision = {}, onApprove = {}
    )
}
