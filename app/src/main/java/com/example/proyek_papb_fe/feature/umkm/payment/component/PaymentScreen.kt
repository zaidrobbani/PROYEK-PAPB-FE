package com.example.proyek_papb_fe.feature.umkm.payment.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Data tampilan Pembayaran (sudah diformat). */
data class PaymentUiState(
    val projectTitle: String,
    val freelancerName: String,
    val freelancerPhotoUri: String?,
    /** "4.9". */
    val freelancerRating: String,
    /** "Desainer Grafis". */
    val freelancerRole: String,
    /** "14 Hari Kalender". */
    val duration: String,
    /** Sisa waktu QRIS, mis. "14:52". */
    val countdown: String,
    val total: String,
    val agreedValue: String,
    /** "Biaya Platform TEMUKARYA (5%)". */
    val platformFeeLabel: String,
    val platformFee: String,
    val freelancerNet: String,
    val isProcessing: Boolean
)

/**
 * Figma: Detail Pembayaran QRIS (17:1267), judul "Pembayaran". Satu kolom scroll seperti
 * Figma (Main pt 67.61 − app bar 64, px 16.9, jarak antar kartu 12.68) dengan tombol
 * "Bayar (simulasi)" di akhir. Saat [PaymentUiState.isProcessing], tombol menampilkan proses
 * dan tidak bisa ditekan lagi.
 */
@Composable
fun PaymentScreen(
    state: PaymentUiState,
    onBack: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Pembayaran", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = P17, end = P17, top = 3.61.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(P13)
        ) {
            StatusCard()
            ProjectCard(state)
            QrisCard(state)
            BreakdownCard(state)
            PayButton(state.isProcessing, onPay, Modifier.padding(top = 4.226.dp))
        }
    }
}

/** 17:1270: "Menunggu pembayaran" + chip "Simulasi". */
@Composable
private fun StatusCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(PaymentColors.StatusCard, PaymentCardShape)
            .padding(P13),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.031.dp).background(PaymentColors.Chip, CircleShape), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_payment_hourglass), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 14.085.dp, height = 17.607.dp))
            }
            Column {
                Text("Menunggu pembayaran", style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary)
                Text("Ini simulasi, tidak ada uang sungguhan.", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
            }
        }
        Text(
            "Simulasi",
            style = PaymentTypography.Caption,
            color = PaymentColors.OnGreen,
            modifier = Modifier
                .background(PaymentColors.Green, CircleShape)
                .padding(start = 10.564.dp, end = 45.817.dp, top = 4.226.dp, bottom = 4.226.dp)
        )
    }
}

/** 17:1283: judul project dan freelancer + durasi kerja. */
@Composable
private fun ProjectCard(state: PaymentUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Text(
            state.projectTitle,
            style = PaymentTypography.Title,
            color = TemuKaryaColors.TextPrimary,
            modifier = Modifier.padding(end = 23.79.dp, top = 3.169.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, PaymentInnerShape).padding(P13),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
                FreelancerAvatar(state.freelancerPhotoUri, 42.256f)
                Column {
                    Text(state.freelancerName, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_payment_star), null, tint = TemuKaryaColors.TextPrimary, modifier = Modifier.size(width = 12.325.dp, height = 11.709.dp))
                        Text(state.freelancerRating, style = PaymentTypography.CaptionSemi, color = TemuKaryaColors.TextBody)
                        Text(state.freelancerRole, style = PaymentTypography.CaptionRegular, color = TemuKaryaColors.TextBody, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Durasi Kerja", style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
                Text(state.duration, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary)
            }
        }
    }
}

/** Foto freelancer (Kelola Profil Profesional) atau foto contoh dari Figma. */
@Composable
internal fun FreelancerAvatar(photoUri: String?, sizeDp: Float) {
    LocalImage(
        uri = photoUri,
        fallbackRes = R.drawable.img_payment_freelancer_avatar,
        contentDescription = null,
        modifier = Modifier.size(sizeDp.dp).clip(CircleShape)
    )
}

/** 17:1311: QRIS, kode QR, batas waktu, total, dan catatan escrow. */
@Composable
private fun QrisCard(state: PaymentUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceWhite, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Text("QRIS", style = PaymentTypography.Qris, color = PaymentColors.Heading)
        Column(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(P17),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(TemuKaryaColors.SurfaceWhite, PaymentInnerShape)
                    .innerShadow(PaymentInnerShape, Shadow(radius = 4.226.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 2.113.dp)))
                    .padding(P13)
            ) {
                Image(painterResource(R.drawable.img_payment_qris), contentDescription = "Kode QRIS (simulasi)", modifier = Modifier.size(202.831.dp))
            }
            Row(
                modifier = Modifier
                    .padding(top = P13)
                    .background(TemuKaryaColors.SurfaceContainer, CircleShape)
                    .padding(horizontal = P13, vertical = 6.338.dp),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_payment_timer), null, tint = PaymentColors.Heading, modifier = Modifier.size(14.085.dp))
                Text(
                    buildAnnotatedString {
                        append("Selesaikan dalam ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PaymentColors.Heading)) { append(state.countdown) }
                        append(" menit")
                    },
                    style = PaymentTypography.BodySemi,
                    color = TemuKaryaColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.226.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Pembayaran", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
            Text(state.total, style = PaymentTypography.Amount, color = PaymentColors.Heading)
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(PaymentColors.EscrowNote, PaymentCardShape).padding(P13),
            horizontalArrangement = Arrangement.spacedBy(P8)
        ) {
            Icon(painterResource(R.drawable.ic_payment_shield), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 14.085.dp, height = 19.72.dp))
            Text(
                "Setelah pembayaran berhasil, dana ditahan TEMUKARYA dan diteruskan ke freelancer setelah kamu menyetujui hasil.",
                style = PaymentTypography.Note,
                color = TemuKaryaColors.TextPrimary,
                modifier = Modifier.weight(1f).padding(end = 5.958.dp)
            )
        }
    }
}

/** 17:1373: "Rincian pembayaran" dan total ditransfer UMKM. */
@Composable
private fun BreakdownCard(state: PaymentUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paymentCardShadow(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Rincian pembayaran", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
            Text(
                "Metode: QRIS",
                style = PaymentTypography.Caption,
                color = PaymentColors.OnMethodChip,
                modifier = Modifier.background(PaymentColors.MethodChip, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(P8)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Nilai Project Disepakati", style = PaymentTypography.Row, color = TemuKaryaColors.TextBody)
                Text(state.agreedValue, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.platformFeeLabel, style = PaymentTypography.Row, color = TemuKaryaColors.TextBody)
                    Icon(painterResource(R.drawable.ic_payment_info), null, tint = TemuKaryaColors.TextPlaceholder, modifier = Modifier.size(12.325.dp))
                }
                Text(state.platformFee, style = PaymentTypography.BodySemi, color = PaymentColors.Olive)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(start = P8), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Diterima Freelancer (Net)", style = PaymentTypography.Body, color = PaymentColors.NetText)
                Text(state.freelancerNet, style = PaymentTypography.Body, color = PaymentColors.NetText)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, PaymentInnerShape).padding(P13),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Ditransfer UMKM", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
            Text(state.total, style = PaymentTypography.TotalBold, color = PaymentColors.Heading)
        }
    }
}

/** 17:1441: tombol "Bayar (simulasi)" 50.7 dengan bayangan; saat proses: indikator + teks proses. */
@Composable
private fun PayButton(isProcessing: Boolean, onPay: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.708.dp)
            .dropShadow(PaymentButtonShape, Shadow(radius = 6.338.dp, spread = (-1.056).dp, color = PaymentColors.ButtonShadow, offset = DpOffset(0.dp, 4.226.dp)))
            .dropShadow(PaymentButtonShape, Shadow(radius = 4.226.dp, spread = (-2.113).dp, color = PaymentColors.ButtonShadow, offset = DpOffset(0.dp, 2.113.dp)))
            .clip(PaymentButtonShape)
            .background(TemuKaryaColors.ActionPrimary)
            .clickable(enabled = !isProcessing, role = Role.Button, onClick = onPay)
            .semantics { if (isProcessing) stateDescription = "Memproses pembayaran" },
        horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TemuKaryaColors.OnAction, strokeWidth = 2.dp)
            Text("Memproses pembayaran…", style = PaymentTypography.Button, color = TemuKaryaColors.OnAction)
        } else {
            Text("Bayar (simulasi)", style = PaymentTypography.Button, color = TemuKaryaColors.OnAction)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PaymentScreenPreview() {
    PaymentScreen(
        state = PaymentUiState(
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            freelancerName = "Andi Ramadhan", freelancerPhotoUri = null, freelancerRating = "4.9", freelancerRole = "Desainer Grafis",
            duration = "14 Hari Kalender", countdown = "14:52", total = "Rp 1.500.000", agreedValue = "Rp 1.500.000",
            platformFeeLabel = "Biaya Platform TEMUKARYA (5%)", platformFee = "Rp 75.000", freelancerNet = "Rp 1.425.000", isProcessing = false
        ),
        onBack = {}, onPay = {}
    )
}
