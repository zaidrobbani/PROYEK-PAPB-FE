package com.example.proyek_papb_fe.feature.umkm.component.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.OnMatchChip
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/** "Cocok 92% · Desain Logo Kopi": kecocokan skill (matching F1) dengan project TERBUKA milik UMKM. */
@Composable
fun FreelancerMatchBadge(percent: Int, projectTitle: String?, modifier: Modifier = Modifier) {
    Text(
        text = "Cocok $percent%" + projectTitle?.let { " · $it" }.orEmpty(),
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
        color = OnMatchChip,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall.withFrameMetrics(
            fontWeight = FontWeight.Bold,
            fontSize = 11.621.sp,
            lineHeight = 16.903.sp
        )
    )
}
