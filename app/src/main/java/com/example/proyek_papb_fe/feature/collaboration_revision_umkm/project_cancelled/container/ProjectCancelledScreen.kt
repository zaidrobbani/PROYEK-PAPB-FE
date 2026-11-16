package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarBorder
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.component.CancelledActionButton
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.component.CancelledDetail
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.component.CancelledDetailCard
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.component.CancelledHero
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CancelledTime = DateTimeFormatter.ofPattern("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID"))

/**
 * Status Project setelah pembatalan (Figma 26:201). Membaca negosiasi yang baru dibatalkan dari
 * [UmkmNegotiationStore] (SIMULASI lokal, bukan data server). Hanya tampil untuk status
 * Dibatalkan; selain itu menampilkan pesan bahwa project tidak dibatalkan.
 */
@Composable
fun ProjectCancelledScreen(
    conversationId: String,
    onBackToProjects: () -> Unit,
    onFindOtherOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val conversation = UmkmConversationStore.conversation(conversationId)
    val negotiation = UmkmNegotiationStore.negotiation(conversationId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StatusAppBar()
        if (conversation == null || negotiation == null || negotiation.status != ProjectStatus.Dibatalkan) {
            // Tidak ada di Figma: pengaman bila screen dibuka untuk project yang tidak dibatalkan.
            Box(Modifier.fillMaxSize().padding(17.dp), contentAlignment = Alignment.Center) {
                Text("Project ini tidak berstatus Dibatalkan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Column
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                // Figma: pt 110 dari atas layar (App Bar 64), pb 32.
                .padding(start = 17.dp, end = 17.dp, top = 110.dp - 64.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CancelledHero(
                statusLabel = negotiation.status.label,
                description = "Kesepakatan tidak dilanjutkan. Tidak ada dana yang ditarik dari akunmu."
            )
            CancelledDetailCard(
                listOfNotNull(
                    CancelledDetail("Project", conversation.projectTitle),
                    CancelledDetail("Freelancer", conversation.freelancerName),
                    negotiation.cancellationReason?.let { CancelledDetail("Alasan", it.label) },
                    negotiation.cancelledAt?.let { CancelledDetail("Waktu", "${it.format(CancelledTime)} WIB") },
                    // Pembatalan hanya diizinkan sebelum pembayaran berhasil (Blueprint).
                    CancelledDetail("Dana", if (negotiation.isPaid) "Sudah dibayar" else "Tidak ada pembayaran", positive = !negotiation.isPaid)
                )
            )
            CancelledActionButton("Kembali ke Project", filled = true, icon = null, onClick = onBackToProjects)
            CancelledActionButton("Cari Pilihan Lain", filled = false, icon = R.drawable.ic_symbol_search, onClick = onFindOtherOptions)
        }
    }
}

/** App Bar Figma tanpa tombol kembali: hanya judul "Status Project". */
@Composable
private fun StatusAppBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(AppBarBorder, Offset(0f, y), Offset(size.width, y), stroke)
            }
            .padding(start = 15.dp, end = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            "Status Project",
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 24.sp
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ProjectCancelledScreenPreview() {
    PROYEKPAPBFETheme {
        ProjectCancelledScreen(conversationId = "dummy-chat-maya", onBackToProjects = {}, onFindOtherOptions = {})
    }
}
