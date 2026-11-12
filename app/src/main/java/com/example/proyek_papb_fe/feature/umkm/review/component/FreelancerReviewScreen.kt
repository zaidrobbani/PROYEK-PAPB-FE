package com.example.proyek_papb_fe.feature.umkm.review.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

/** Keunggulan yang bisa dipilih UMKM (Figma 17:6715). */
val ReviewHighlightOptions = listOf(
    "Responsif & Komunikatif",
    "Hasil Siap Cetak Sempurna",
    "Tepat Waktu",
    "Paham Kebutuhan UMKM"
)

/** Data Beri Ulasan (Figma 17:6591). */
data class FreelancerReviewUiState(
    val freelancerName: String,
    val freelancerFirstName: String,
    val freelancerPhotoUri: String?,
    val projectTitle: String,
    /** "24 Okt 2026". */
    val completedDate: String,
    val rating: Int,
    val highlights: Set<String>,
    val text: String,
    /** Gambar hasil yang disertakan; null = draf tidak memuat gambar. */
    val attachment: ReviewFileUi?,
    val includeAttachment: Boolean
) {
    val canSubmit: Boolean get() = rating in 1..5
}

private val SmallCardShadow = Shadow(radius = 1.056.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp))

/**
 * Figma: Beri Ulasan Freelancer (17:6591). Bintang wajib dipilih sebelum "Kirim ulasan";
 * keunggulan, teks, dan lampiran opsional. "Lewati untuk Sekarang" kembali ke Beranda.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreelancerReviewScreen(
    state: FreelancerReviewUiState,
    onBack: () -> Unit,
    onRate: (Int) -> Unit,
    onToggleHighlight: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onToggleAttachment: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas).imePadding()) {
        DetailTopBar(title = "Beri Ulasan", onBack = onBack)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = P17, end = P17, top = 3.61.dp, bottom = 33.805.dp),
            verticalArrangement = Arrangement.spacedBy(P13)
        ) {
            // 17:6605: kartu freelancer & project.
            Row(
                modifier = Modifier.fillMaxWidth().dropShadow(PaymentCardShape, SmallCardShadow).background(TemuKaryaColors.SurfaceContainer, PaymentCardShape).padding(P17),
                horizontalArrangement = Arrangement.spacedBy(14.79.dp)
            ) {
                Box {
                    FreelancerAvatar(state.freelancerPhotoUri, 59.159f)
                    Box(
                        Modifier.align(Alignment.BottomEnd).offset(x = 2.113.dp, y = 2.113.dp).size(21.128.dp).background(PaymentColors.Olive, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_ul_check_tiny), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(width = 9.327.dp, height = 6.881.dp))
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.113.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalArrangement = Arrangement.spacedBy(6.338.dp), itemVerticalAlignment = Alignment.CenterVertically) {
                        Text(state.freelancerName, style = PaymentTypography.Title, color = TemuKaryaColors.TextPrimary)
                        Text(
                            "Terverifikasi",
                            style = PaymentTypography.Caption,
                            color = TemuKaryaColors.TextBody,
                            modifier = Modifier.background(PaymentColors.Chip, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
                        )
                    }
                    Text(state.projectTitle, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(modifier = Modifier.padding(top = 5.81.dp), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_ul_check_circle), null, tint = PaymentColors.Olive, modifier = Modifier.size(12.325.dp))
                            Text("Selesai", style = PaymentTypography.Caption, color = PaymentColors.Olive)
                        }
                        Text(state.completedDate, style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
                    }
                }
            }
            // 17:6630: rating keseluruhan.
            Column(
                modifier = Modifier.fillMaxWidth().dropShadow(PaymentCardShape, SmallCardShadow).background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
                    .padding(start = P17, end = P17, top = 20.6.dp, bottom = P17),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.754.dp)
            ) {
                Text("Bagaimana Kinerja ${state.freelancerFirstName}?", style = ReviewTypography.SheetTitle.copy(letterSpacing = ReviewTypography.Heading.letterSpacing), color = TemuKaryaColors.TextPrimary, textAlign = TextAlign.Center)
                StarRow(state.rating, 31.692.dp, 30.108.dp, 6.338.dp, 4.226.dp, onRate, Modifier.padding(vertical = 10.036.dp))
                Row(
                    modifier = Modifier.background(PaymentColors.StatusCard, CircleShape).padding(horizontal = P13, vertical = 6.338.dp),
                    horizontalArrangement = Arrangement.spacedBy(P8),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (state.rating > 0) "${state.rating}.0 / 5.0" else "– / 5.0", style = ReviewTypography.Score, color = PaymentColors.Heading)
                    Box(Modifier.size(width = 5.493.dp, height = 6.338.dp).background(ReviewColors.Muted, CircleShape))
                    Text(sentimentLabel(state.rating), style = PaymentTypography.CaptionSemi, color = PaymentColors.Olive, textAlign = TextAlign.Center, modifier = Modifier.weight(1f, fill = false))
                }
            }
            // 17:6709: keunggulan utama.
            Column(
                modifier = Modifier.fillMaxWidth().dropShadow(PaymentCardShape, SmallCardShadow).background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(P17),
                verticalArrangement = Arrangement.spacedBy(P8)
            ) {
                Text("Keunggulan Utama ${state.freelancerFirstName}", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(P8), verticalArrangement = Arrangement.spacedBy(P8)) {
                    ReviewHighlightOptions.forEach { option ->
                        val selected = option in state.highlights
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selected) PaymentColors.Green else PaymentColors.StatusCard)
                                .clickable(role = Role.Checkbox) { onToggleHighlight(option) }
                                .padding(horizontal = P13, vertical = 6.338.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selected) Icon(painterResource(R.drawable.ic_ul_tag_check), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(14.085.dp))
                            else Icon(painterResource(R.drawable.ic_rq_plus), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(9.86.dp))
                            Text(option, style = PaymentTypography.Caption, color = if (selected) PaymentColors.OnGreen else TemuKaryaColors.TextBody)
                        }
                    }
                }
            }
            // 17:6747: ulasan tertulis + lampiran.
            Column(
                modifier = Modifier.fillMaxWidth().dropShadow(PaymentCardShape, SmallCardShadow).background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(P17),
                verticalArrangement = Arrangement.spacedBy(4.226.dp)
            ) {
                Text("Ulasan & Testimoni Tertulis", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TemuKaryaColors.SurfaceWhite, PaymentCardShape)
                        .innerShadow(PaymentCardShape, Shadow(radius = 4.226.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 2.113.dp)))
                        .padding(start = P13, end = P13, top = P13, bottom = 25.354.dp)
                ) {
                    BasicTextField(
                        value = state.text,
                        onValueChange = { onTextChange(it.take(500)) },
                        textStyle = ReviewTypography.Textarea.copy(color = TemuKaryaColors.TextPrimary),
                        cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 84.513.dp),
                        decorationBox = { inner ->
                            Box {
                                if (state.text.isEmpty()) {
                                    Text("Ceritakan pengalamanmu bekerja dengan ${state.freelancerFirstName}…", style = ReviewTypography.Textarea, color = ReviewColors.Muted)
                                }
                                inner()
                            }
                        }
                    )
                }
                state.attachment?.let { file ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.226.dp).background(TemuKaryaColors.SurfaceContainer, PaymentInnerShape)
                            .padding(start = 10.564.dp, end = 10.564.dp, top = P13, bottom = 10.564.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.564.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LocalImage(
                            uri = file.uri,
                            fallbackRes = null,
                            contentDescription = file.name,
                            modifier = Modifier.size(50.708.dp).alpha(if (state.includeAttachment) 1f else 0.4f).dropShadow(RoundedCornerShape(6.338.dp), SmallCardShadow).clip(RoundedCornerShape(6.338.dp)).background(PaymentColors.StatusCard)
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.585.dp)) {
                            Text(
                                if (state.includeAttachment) "Hasil Desain Disertakan" else "Lampiran tidak disertakan",
                                style = PaymentTypography.Caption,
                                color = TemuKaryaColors.TextPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.ic_ul_image), null, tint = PaymentColors.Olive, modifier = Modifier.size(12.325.dp))
                                Text(file.name, style = PaymentTypography.Body, color = PaymentColors.Olive, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        RoundIconButton(
                            R.drawable.ic_ul_edit, 33.805.dp, 14.262.dp, 14.262.dp, androidx.compose.ui.graphics.Color.Transparent, TemuKaryaColors.TextPrimary,
                            if (state.includeAttachment) "Lepas lampiran" else "Sertakan lampiran",
                            onToggleAttachment
                        )
                    }
                }
            }
            // 17:6806: CTA.
            Column(modifier = Modifier.padding(top = 4.226.dp), verticalArrangement = Arrangement.spacedBy(P8)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.708.dp)
                        .alpha(if (state.canSubmit) 1f else 0.45f)
                        .dropShadow(RoundedCornerShape(12.dp), Shadow(radius = 6.338.dp, color = PaymentColors.ButtonShadow, offset = DpOffset(0.dp, 4.226.dp)))
                        .clip(RoundedCornerShape(12.dp))
                        .background(TemuKaryaColors.ActionPrimary)
                        .clickable(enabled = state.canSubmit, role = Role.Button, onClick = onSubmit)
                        .padding(horizontal = P17, vertical = 15.846.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kirim ulasan", style = ReviewTypography.ButtonBold, color = TemuKaryaColors.OnAction)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button, onClick = onSkip).padding(vertical = 10.564.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lewati untuk Sekarang", style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextBody)
                }
            }
        }
    }
}
