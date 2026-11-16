package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Figma: header section ("Project Aktif Saya" / "Rekomendasi Proyek" + "Lihat Semua"). */
@Composable
internal fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionText: String = "Lihat Semua"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = DashboardTypography.SectionTitle,
            color = DashboardColors.TextHeading
        )
        Text(
            text = actionText,
            style = DashboardTypography.LabelSemiBold,
            color = DashboardColors.TextHeading,
            modifier = Modifier.clickable(onClick = onSeeAllClick)
        )
    }
}
