package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/** Warna chip "Menunggu Review" Figma (token status menunggu-review); belum ada di tema. */
private val ReviewStatusChip = Color(0xFFE9C46A)
private val OnReviewStatusChip = Color(0xFF4B3600)

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(fontWeight = weight, fontSize = size, lineHeight = lineHeight)

private fun Modifier.softShadow(shape: androidx.compose.ui.graphics.Shape, color: Color) =
    dropShadow(shape, Shadow(radius = 16.903.dp, color = color, spread = (-2.113).dp, offset = DpOffset(0.dp, 4.226.dp)))

/** Kartu konteks: judul project, UMKM, chip status, pengirim, waktu kirim, dan versi. */
@Composable
fun CompareContextCard(
    projectTitle: String,
    ownerLine: String,
    statusLabel: String,
    statusIsReview: Boolean,
    freelancerInitials: String,
    freelancerName: String,
    sentTime: String,
    versionLine: String
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(shape, colors.primary.copy(alpha = 0.06f))
            .background(colors.tertiaryContainer, shape)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(projectTitle, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, style = text(FontWeight.SemiBold, 16.903.sp, 23.241.sp))
                Text(ownerLine, color = colors.onSurfaceVariant, maxLines = 1, style = text(FontWeight.Normal, 12.677.sp, 16.903.sp))
            }
            // Figma menggambar "Menunggu Review"; status lain memakai chip pasir yang senada.
            Row(
                modifier = Modifier
                    .background(if (statusIsReview) ReviewStatusChip else colors.surfaceVariant, CircleShape)
                    .padding(horizontal = 10.564.dp, vertical = 4.226.dp),
                horizontalArrangement = Arrangement.spacedBy(4.215.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (statusIsReview) Image(painterResource(R.drawable.ic_compare_review_status), null)
                Text(
                    statusLabel,
                    color = if (statusIsReview) OnReviewStatusChip else colors.onSurfaceVariant,
                    maxLines = 1,
                    style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(25.354.dp).background(colors.secondary, CircleShape), contentAlignment = Alignment.Center) {
                    Text(freelancerInitials, color = colors.onPrimary, style = text(FontWeight.Medium, 11.621.sp, 14.79.sp))
                }
                Text(freelancerName, color = colors.onSurface, style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp))
                Text(sentTime, color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 11.621.sp, 14.79.sp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_compare_version), null)
                Text(versionLine, color = colors.onTertiaryContainer, style = text(FontWeight.SemiBold, 11.621.sp, 14.79.sp))
            }
        }
    }
}

/** Data satu kolom perbandingan. [latest] = kolom kanan (perbaikan terbaru). */
data class DraftColumn(
    val headerLabel: String,
    val headerTrailing: String,
    @param:DrawableRes val image: Int?,
    val previewTag: String,
    val noteTitle: String,
    val note: String,
    val markers: List<Pair<Float, Float>>,
    val latest: Boolean
)

/** Dua kolom berdampingan seperti "View 1: Side by Side (Default)" di Figma. */
@Composable
fun SideBySideComparison(previous: DraftColumn, latest: DraftColumn) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.451.dp)) {
        DraftPanel(previous)
        DraftPanel(latest)
    }
}

@Composable
private fun RowScope.DraftPanel(column: DraftColumn) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .weight(1f)
            .softShadow(shape, colors.primary.copy(alpha = 0.06f))
            .clip(shape)
            .background(colors.surfaceContainerLowest)
            .semantics { contentDescription = column.headerLabel }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (column.latest) SageBadge else colors.tertiaryContainer)
                .padding(horizontal = 10.564.dp, vertical = 6.338.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                column.headerLabel,
                color = if (column.latest) OnSageBadge else colors.onSurfaceVariant,
                maxLines = 1,
                style = text(if (column.latest) FontWeight.Bold else FontWeight.SemiBold, 11.621.sp, 14.79.sp)
            )
            Text(
                column.headerTrailing,
                color = if (column.latest) OnSageBadge else colors.outline,
                maxLines = 1,
                style = text(if (column.latest) FontWeight.SemiBold else FontWeight.Medium, 11.621.sp, 14.79.sp)
            )
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(colors.surfaceContainerHigh)
        ) {
            if (column.image != null) {
                Image(
                    painterResource(column.image),
                    contentDescription = "Pratinjau ${column.headerLabel}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            column.markers.forEachIndexed { index, (x, y) ->
                Box(
                    modifier = Modifier
                        .offset(x = maxWidth * x, y = maxHeight * y)
                        .size(21.128.dp)
                        .dropShadow(CircleShape, Shadow(radius = 6.338.dp, color = Color.Black.copy(alpha = 0.1f), spread = (-1.056).dp, offset = DpOffset(0.dp, 4.226.dp)))
                        .background(if (column.latest) colors.secondary else colors.error, CircleShape)
                        .semantics { contentDescription = if (column.latest) "Bagian ${index + 1} diperbaiki" else "Bagian bermasalah ${index + 1}" },
                    contentAlignment = Alignment.Center
                ) {
                    if (column.latest) {
                        Image(painterResource(R.drawable.ic_compare_fixed), null)
                    } else {
                        Text("${index + 1}", color = colors.onError, style = text(FontWeight.Bold, 11.621.sp, 14.79.sp))
                    }
                }
            }
            Text(
                column.previewTag,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.34.dp)
                    .background(
                        if (column.latest) colors.primary.copy(alpha = 0.8f) else colors.onSurface.copy(alpha = 0.7f),
                        RoundedCornerShape(4.226.dp)
                    )
                    .padding(horizontal = 6.338.dp, vertical = 2.113.dp),
                color = if (column.latest) colors.onPrimary else colors.background,
                style = text(FontWeight.Normal, 10.564.sp, 15.846.sp)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.451.dp),
            verticalArrangement = Arrangement.spacedBy(1.453.dp)
        ) {
            Text(
                column.noteTitle,
                color = if (column.latest) colors.secondary else colors.error,
                style = text(FontWeight.SemiBold, 11.621.sp, 14.79.sp)
            )
            Text(
                column.note,
                color = if (column.latest) colors.onSurface else colors.onSurfaceVariant,
                style = text(FontWeight.Normal, 11.621.sp, 14.526.sp)
            )
        }
    }
}

/** Baki keputusan: Diskusi (ikon), Minta Revisi, dan Setujui Draf & Selesai (lebar 1 : 1,4). */
@Composable
fun CompareDecisionTray(
    onDiscuss: () -> Unit,
    onRequestRevision: () -> Unit,
    onApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(RectangleShape, Shadow(radius = 16.903.dp, color = colors.primary.copy(alpha = 0.08f), offset = DpOffset(0.dp, (-4.226).dp)))
            .background(colors.background.copy(alpha = 0.95f))
            .padding(horizontal = 16.903.dp, vertical = 12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.708.dp)
                .clip(shape)
                .background(colors.surfaceContainerHigh)
                .clickable(role = Role.Button, onClick = onDiscuss)
                .semantics { contentDescription = "Buka diskusi dengan desainer" },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(R.drawable.ic_compare_discuss), null)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.708.dp)
                .clip(shape)
                .background(colors.surfaceContainerLow)
                .clickable(role = Role.Button, onClick = onRequestRevision)
                .padding(horizontal = 8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.ic_compare_request_revision), null)
            Text("Minta Revisi", color = colors.onTertiaryContainer, maxLines = 1, style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp))
        }
        Row(
            modifier = Modifier
                .weight(1.4f)
                .height(50.708.dp)
                .dropShadow(shape, Shadow(radius = 6.338.dp, color = Color.Black.copy(alpha = 0.1f), spread = (-1.056).dp, offset = DpOffset(0.dp, 4.226.dp)))
                .clip(shape)
                .background(colors.primary)
                .clickable(role = Role.Button, onClick = onApprove)
                .padding(horizontal = 12.677.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.ic_compare_approve), null)
            Text("Setujui Draf & Selesai", color = colors.onPrimary, maxLines = 1, style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp))
        }
    }
}
