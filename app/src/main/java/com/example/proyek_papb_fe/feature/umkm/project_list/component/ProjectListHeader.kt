package com.example.proyek_papb_fe.feature.umkm.project_list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus

@Composable
fun ProjectAttentionBanner(
    reviewCount: Int,
    offerCount: Int,
    actionCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .registerInputShadow(shape)
            .background(colors.tertiaryContainer, shape)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.031.dp)
                .background(colors.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_project_list_attention),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.321.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Perlu Perhatian Anda",
                    color = colors.onTertiaryContainer,
                    style = MaterialTheme.typography.titleSmall.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.79.sp,
                        lineHeight = 19.015.sp
                    )
                )
                Text(
                    text = "$actionCount Aksi",
                    modifier = Modifier
                        .background(colors.errorContainer.copy(alpha = 0.6f), CircleShape)
                        .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
                    color = colors.error,
                    style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.621.sp,
                        lineHeight = 14.79.sp
                    )
                )
            }
            Text(
                text = "$reviewCount hasil kerja siap di-review dan $offerCount penawaran baru " +
                    "menunggu keputusan untuk proyek digital Anda.",
                modifier = Modifier.padding(bottom = 0.792.dp),
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 17.431.sp
                )
            )
        }
    }
}

enum class ProjectTab(val label: String, val statuses: Set<ProjectStatus>?) {
    Semua("Semua", null),
    MenungguKesepakatan(
        "Menunggu Kesepakatan",
        setOf(ProjectStatus.Terbuka, ProjectStatus.MenungguKesepakatan, ProjectStatus.MenungguPembayaran)
    ),
    SedangDikerjakan(
        "Sedang Dikerjakan",
        setOf(ProjectStatus.SiapDikerjakan, ProjectStatus.SedangDikerjakan, ProjectStatus.Revisi)
    ),
    MenungguReview("Menunggu Review", setOf(ProjectStatus.MenungguReview)),
    Selesai("Selesai", setOf(ProjectStatus.Selesai)),
    Dibatalkan("Dibatalkan", setOf(ProjectStatus.Dibatalkan));

    fun matches(status: ProjectStatus): Boolean = statuses == null || status in statuses
}

@Composable
fun ProjectStatusTabs(
    selected: ProjectTab,
    counts: Map<ProjectTab, Int>,
    onSelect: (ProjectTab) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = CircleShape

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        items(ProjectTab.entries) { tab ->
            val isSelected = tab == selected
            Text(
                text = "${tab.label} (${counts[tab] ?: 0})",
                modifier = Modifier
                    .then(if (isSelected) Modifier.authDropShadow(shape, AuthShadowStyle.Subtle) else Modifier)
                    .clip(shape)
                    .background(if (isSelected) colors.primary else colors.tertiaryContainer)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(tab) })
                    .padding(horizontal = 16.903.dp, vertical = 8.451.dp),
                color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
    }
}
