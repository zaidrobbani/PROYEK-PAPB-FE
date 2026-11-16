package com.example.proyek_papb_fe.feature.umkm.payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
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
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Data tampilan Bukti Pembayaran (sudah diformat, dari pembayaran yang sama dengan layar Pembayaran). */
data class PaymentProofUiState(
    val paymentId: String,
    /** "24 Okt 2026, 14.32 WIB". */
    val paidAt: String,
    val total: String,
    val contractValue: String,
    /** "QRIS (simulasi)". */
    val methodLabel: String,
    val projectTitle: String,
    val freelancerName: String,
    val freelancerRole: String,
    val freelancerPhotoUri: String?,
    val umkmName: String,
    val umkmOwner: String
)

/**
 * Figma: Bukti Pembayaran Berhasil (17:3177), judul "Bukti Pembayaran". Hero sukses lalu
 * kartu struk digital escrow. Figma tidak memiliki tombol aksi; keluar lewat Back.
 */
@Composable
fun PaymentProofScreen(
    state: PaymentProofUiState,
    onBack: () -> Unit,
    onCopyPaymentId: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Bukti Pembayaran", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = P17, end = P17, top = 3.61.dp, bottom = 24.dp)
        ) {
            SuccessHero()
            Receipt(state, onCopyPaymentId, Modifier.padding(top = P17))
        }
    }
}

/** 17:3180: lingkaran sukses 67.6 dengan titik hijau, judul, dan keterangan. */
@Composable
private fun SuccessHero() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = P8), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.padding(bottom = P13)) {
            Box(
                modifier = Modifier.size(67.61.dp).paymentCardShadow(CircleShape).background(PaymentColors.Green, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_payment_success), null, tint = PaymentColors.Olive, modifier = Modifier.size(31.692.dp))
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.23.dp, y = (-4.23).dp)
                    .size(16.903.dp)
                    .background(PaymentColors.Olive, CircleShape)
            )
        }
        Text("Pembayaran berhasil", style = PaymentTypography.Hero, color = PaymentColors.Heading, textAlign = TextAlign.Center)
        Text(
            "Dana ditahan TEMUKARYA sampai kamu menyetujui hasil kerja.",
            style = PaymentTypography.Subtitle,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 5.678.dp).widthIn(max = 316.923.dp)
        )
    }
}

/** 17:3242: "Kartu Struk Digital Escrow". */
@Composable
private fun Receipt(state: PaymentProofUiState, onCopy: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(PaymentCardShape, Shadow(radius = 2.113.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))
            .clip(PaymentCardShape)
            .background(TemuKaryaColors.SurfaceWhite)
    ) {
        // 17:3306: bagian atas struk.
        Column(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard).padding(P17),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.background(PaymentColors.Green, CircleShape).padding(horizontal = P13, vertical = 4.226.dp),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_payment_lock), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(width = 11.268.dp, height = 14.79.dp))
                Text("Dana ditahan", style = PaymentTypography.BodySemi, color = PaymentColors.OnGreen)
            }
            Text("Total Nominal Dibayar", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, modifier = Modifier.padding(top = P13))
            Text(state.total, style = PaymentTypography.Amount, color = PaymentColors.Heading, modifier = Modifier.padding(top = 2.113.dp))
            Row(modifier = Modifier.padding(top = P8), horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_payment_qris_small), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(12.677.dp))
                Text(state.methodLabel, style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
            }
        }
        Perforation()
        // 17:3243: rincian bawah struk.
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = P17, end = P17, top = 4.226.dp, bottom = P17),
            verticalArrangement = Arrangement.spacedBy(14.79.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Nomor Transaksi", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                Row(
                    modifier = Modifier
                        .clip(PaymentInnerShape)
                        .background(TemuKaryaColors.SurfaceContainer)
                        .clickable(role = Role.Button, onClickLabel = "Salin nomor transaksi", onClick = onCopy)
                        .padding(horizontal = P8, vertical = 4.226.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.paymentId, style = PaymentTypography.BodySemi, color = PaymentColors.Heading)
                    Icon(painterResource(R.drawable.ic_payment_copy), "Salin", tint = PaymentColors.Heading, modifier = Modifier.size(width = 11.973.dp, height = 14.085.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Waktu Transaksi", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                Text(state.paidAt, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
                    .padding(start = 10.564.dp, end = 10.564.dp, top = 4.226.dp, bottom = 10.564.dp),
                verticalArrangement = Arrangement.spacedBy(3.301.dp)
            ) {
                Text("Proyek Pesanan", style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody)
                Text(state.projectTitle, style = PaymentTypography.ProjectInReceipt, color = PaymentColors.Heading)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.226.dp), horizontalArrangement = Arrangement.spacedBy(P8)) {
                PartyCard("Mitra Freelancer", state.freelancerName, state.freelancerRole, Modifier.weight(1f)) {
                    FreelancerAvatar(state.freelancerPhotoUri, 29.579f)
                }
                PartyCard("Pemilik UMKM", state.umkmName, state.umkmOwner, Modifier.weight(1f)) {
                    Box(Modifier.size(29.579.dp).background(PaymentColors.UmkmAvatar, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_payment_store), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 12.677.dp, height = 11.268.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(top = P8), verticalArrangement = Arrangement.spacedBy(P8)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Nilai Kontrak Proyek", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                    Text(state.contractValue, style = PaymentTypography.Row, color = TemuKaryaColors.TextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = P8),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Terbayar", style = PaymentTypography.RowSemi, color = TemuKaryaColors.TextPrimary)
                    Text(state.total, style = PaymentTypography.TotalBold, color = PaymentColors.Heading)
                }
            }
        }
    }
}

/** 17:3324: lekukan kiri-kanan (warna kanvas) dan garis putus-putus. */
@Composable
private fun Perforation() {
    Row(modifier = Modifier.fillMaxWidth().height(25.354.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(P17).fillMaxHeight().background(TemuKaryaColors.BgCanvas, RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp)))
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = P8)
                .height(1.056.dp)
                .drawBehind {
                    drawLine(
                        color = PaymentColors.Divider,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))
                    )
                }
        )
        Box(Modifier.width(P17).fillMaxHeight().background(TemuKaryaColors.BgCanvas, RoundedCornerShape(topStart = 999.dp, bottomStart = 999.dp)))
    }
}

@Composable
private fun PartyCard(label: String, name: String, subtitle: String, modifier: Modifier, avatar: @Composable () -> Unit) {
    Column(modifier = modifier.background(TemuKaryaColors.SurfaceCard, PaymentCardShape).padding(10.564.dp)) {
        Text(label, style = PaymentTypography.Caption, color = TemuKaryaColors.TextBody, modifier = Modifier.padding(bottom = 6.338.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
            avatar()
            Column {
                Text(name, style = PaymentTypography.BodySemi, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PaymentProofScreenPreview() {
    PaymentProofScreen(
        state = PaymentProofUiState(
            paymentId = "TMK-20261024-88492", paidAt = "24 Okt 2026, 14.32 WIB", total = "Rp 1.500.000", contractValue = "Rp 1.500.000",
            methodLabel = "QRIS (simulasi)", projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi",
            freelancerName = "Andi Ramadhan", freelancerRole = "Desainer Grafis", freelancerPhotoUri = null,
            umkmName = "Kedai Kopi Senja", umkmOwner = "Rina Pratiwi"
        ),
        onBack = {}, onCopyPaymentId = {}
    )
}
