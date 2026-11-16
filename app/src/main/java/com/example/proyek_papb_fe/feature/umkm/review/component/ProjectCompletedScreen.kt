package com.example.proyek_papb_fe.feature.umkm.review.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
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

/** Data Proyek Selesai (Figma 17:6338), sudah diformat. */
data class ProjectCompletedUiState(
    val freelancerName: String,
    val freelancerPhotoUri: String?,
    val files: List<ReviewFileUi>,
    /** "27.7 MB"; null = ukuran total tidak diketahui. */
    val totalSize: String?,
    val freelancerNet: String,
    val transactionId: String,
    /** Rating ulasan yang sudah dikirim; 0 = belum diulas. */
    val rating: Int
)

/**
 * Figma: Sukses Penyelesaian Proyek (17:6338), judul "Proyek Selesai". Berkas master,
 * ringkasan dana diteruskan, ajakan memberi ulasan, "Beri ulasan" (17:6591) dan
 * "Kembali ke Beranda".
 */
@Composable
fun ProjectCompletedScreen(
    state: ProjectCompletedUiState,
    onBack: () -> Unit,
    onDownloadFile: (ReviewFileUi) -> Unit,
    onDownloadAll: () -> Unit,
    onGiveReview: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Proyek Selesai", onBack = onBack)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 130.dp)) {
                Celebration(state.freelancerName)
                Column(modifier = Modifier.padding(horizontal = P17), verticalArrangement = Arrangement.spacedBy(P17)) {
                    DeliverablesCard(state, onDownloadFile, onDownloadAll)
                    SettlementCard(state)
                    RatingCallout(state, onGiveReview)
                }
            }
            CompletedActionBar(state.rating > 0, onGiveReview, onBackToHome, Modifier.align(Alignment.BottomCenter))
        }
    }
}

/** 17:6341: pita perayaan. */
@Composable
private fun Celebration(freelancerName: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = P17, end = P17, top = P17, bottom = P13),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.486.dp)
    ) {
        Row(
            modifier = Modifier
                .dropShadow(CircleShape, Shadow(radius = 4.226.dp, color = Color(0x0F684738), offset = DpOffset(0.dp, 2.113.dp)))
                .background(PaymentColors.Green, CircleShape)
                .padding(horizontal = P13, vertical = 4.226.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_dn_verified), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(width = 17.431.dp, height = 16.638.dp))
            Text("Proyek selesai", style = ReviewTypography.Pill, color = PaymentColors.OnGreen)
        }
        Text("Proyek selesai", style = PaymentTypography.Hero, color = PaymentColors.Heading, modifier = Modifier.padding(top = 9.191.dp))
        Text(
            "Dana sudah diteruskan ke dompet $freelancerName. File final bisa kamu unduh di bawah.",
            style = PaymentTypography.Subtitle,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.padding(top = 9.191.dp).background(PaymentColors.StatusCard, CircleShape).padding(horizontal = P13, vertical = 4.226.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_dn_lock), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 10.564.dp, height = 13.865.dp))
            Text("Dana diteruskan", style = PaymentTypography.Caption, color = PaymentColors.Heading)
        }
    }
}

/** 17:6468: Berkas Master Siap Pakai. */
@Composable
private fun DeliverablesCard(state: ProjectCompletedUiState, onDownloadFile: (ReviewFileUi) -> Unit, onDownloadAll: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().paymentCardShadow(PaymentCardShape).background(TemuKaryaColors.SurfaceContainer, PaymentCardShape).padding(P17),
        verticalArrangement = Arrangement.spacedBy(P13)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Berkas Master Siap Pakai", style = PaymentTypography.Title, color = PaymentColors.Heading)
                Text("Tersimpan permanen di Cloud Proyek Anda", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
            }
            Icon(painterResource(R.drawable.ic_dn_cloud), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 23.241.dp, height = 16.903.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(P8)) {
            state.files.forEachIndexed { index, file ->
                val vector = file.extension in setOf("ai", "eps", "svg")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .dropShadow(PaymentInnerShape, Shadow(radius = 1.056.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))
                        .background(TemuKaryaColors.SurfaceWhite, PaymentInnerShape)
                        .padding(start = P13, top = P13, bottom = P13),
                    horizontalArrangement = Arrangement.spacedBy(P13),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(42.256.dp).background(if (vector || index % 2 == 1) PaymentColors.Green else PaymentColors.StatusCard, PaymentInnerShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            vector -> Icon(painterResource(R.drawable.ic_dn_vector), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(width = 18.399.dp, height = 17.431.dp))
                            file.isImage -> Icon(painterResource(R.drawable.ic_rv_image), null, tint = PaymentColors.Heading, modifier = Modifier.size(19.368.dp))
                            else -> Icon(painterResource(R.drawable.ic_dn_pdf), null, tint = PaymentColors.Heading, modifier = Modifier.size(19.368.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(file.name, style = PaymentTypography.RowSemi, color = PaymentColors.Heading, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(file.meta, style = PaymentTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    RoundIconButton(R.drawable.ic_dn_download, 42.256.dp, 14.085.dp, 14.085.dp, Color.Transparent, PaymentColors.Heading, "Unduh ${file.name}") { onDownloadFile(file) }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PaymentInnerShape)
                .background(PaymentColors.Green)
                .clickable(role = Role.Button, onClick = onDownloadAll)
                .padding(vertical = P13),
            horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_dn_zip), null, tint = PaymentColors.OnGreen, modifier = Modifier.size(width = 15.846.dp, height = 12.677.dp))
            Text(
                "Unduh Semua Berkas ZIP" + (state.totalSize?.let { " ($it)" } ?: ""),
                style = PaymentTypography.Button,
                color = PaymentColors.OnGreen
            )
        }
    }
}

/** 17:6510: Dana diteruskan. */
@Composable
private fun SettlementCard(state: ProjectCompletedUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(PaymentCardShape, Shadow(radius = 1.056.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))
            .background(TemuKaryaColors.SurfaceCard, PaymentCardShape)
            .padding(P13),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = P8), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_dn_wallet), null, tint = PaymentColors.Heading, modifier = Modifier.size(width = 18.399.dp, height = 17.431.dp))
            Text("Dana diteruskan", style = PaymentTypography.RowSemi, color = PaymentColors.Heading, modifier = Modifier.weight(1f).padding(start = P8))
            Text(
                "Selesai",
                style = ReviewTypography.CaptionBold,
                color = PaymentColors.OnGreen,
                modifier = Modifier.background(PaymentColors.Green, CircleShape).padding(horizontal = P8, vertical = 2.113.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceWhite, PaymentInnerShape).padding(P13),
            verticalArrangement = Arrangement.spacedBy(P8)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Diterima freelancer", style = PaymentTypography.Row, color = TemuKaryaColors.TextBody)
                Text(state.freelancerNet, style = PaymentTypography.Title, color = PaymentColors.Heading)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tujuan:", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                Text("Dompet ${state.freelancerName}", style = PaymentTypography.Body, color = PaymentColors.Heading)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bukti Transaksi:", style = PaymentTypography.Body, color = TemuKaryaColors.TextBody)
                Text(state.transactionId, style = PaymentTypography.Body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = PaymentColors.Heading)
            }
        }
    }
}

/** 17:6535: ajakan memberi ulasan (bintang membuka layar Beri Ulasan). */
@Composable
private fun RatingCallout(state: ProjectCompletedUiState, onGiveReview: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(PaymentCardShape, Shadow(radius = 2.113.dp, color = PaymentColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))
            .background(ReviewColors.RatingCallout, PaymentCardShape)
            .padding(P17),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Box(
            modifier = Modifier
                .size(59.159.dp)
                .dropShadow(CircleShape, Shadow(radius = 6.338.dp, color = PaymentColors.ButtonShadow, offset = DpOffset(0.dp, 4.226.dp)))
                .background(TemuKaryaColors.SurfaceWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            FreelancerAvatar(state.freelancerPhotoUri, 50.708f)
        }
        Text(
            if (state.rating > 0) "Terima kasih atas ulasanmu!" else "Beri Ulasan untuk ${state.freelancerName}",
            style = PaymentTypography.Title,
            color = PaymentColors.Heading,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.226.dp)
        )
        Text(
            "Ulasanmu membantu UMKM lain memilih freelancer.",
            style = PaymentTypography.Body,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center
        )
        StarRow(
            rating = state.rating,
            starWidth = 24.65.dp,
            starHeight = 23.417.dp,
            gap = 4.226.dp,
            padding = 4.226.dp,
            onRate = if (state.rating > 0) null else { _ -> onGiveReview() },
            modifier = Modifier.padding(top = P8)
        )
        Text(
            if (state.rating > 0) "${sentimentLabel(state.rating)} (${state.rating}.0)" else "Ketuk bintang untuk menilai",
            style = PaymentTypography.Caption,
            color = PaymentColors.Heading,
            textAlign = TextAlign.Center
        )
    }
}

/** 17:6561: Sticky Bottom Action Bar. */
@Composable
private fun CompletedActionBar(reviewed: Boolean, onGiveReview: () -> Unit, onBackToHome: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(RectangleShape, Shadow(radius = 21.128.dp, color = ReviewColors.ShadowWarm, offset = DpOffset(0.dp, (-4.226).dp)))
            .background(ReviewColors.BottomBarCompleted)
            .padding(horizontal = P17, vertical = P8),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        if (!reviewed) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.708.dp)
                    .dropShadow(RoundedCornerShape(12.dp), Shadow(radius = 8.451.dp, color = Color(0x33684738), offset = DpOffset(0.dp, 4.226.dp)))
                    .clip(RoundedCornerShape(12.dp))
                    .background(TemuKaryaColors.ActionPrimary)
                    .clickable(role = Role.Button, onClick = onGiveReview),
                horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_dn_review), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(width = 19.368.dp, height = 18.487.dp))
                Text("Beri ulasan", style = PaymentTypography.Button, color = TemuKaryaColors.OnAction)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.482.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TemuKaryaColors.SurfaceContainer)
                .clickable(role = Role.Button, onClick = onBackToHome),
            contentAlignment = Alignment.Center
        ) {
            Text("Kembali ke Beranda", style = PaymentTypography.Button, color = PaymentColors.Heading)
        }
    }
}
