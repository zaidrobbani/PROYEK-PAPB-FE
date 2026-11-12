package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/**
 * Pill filter status di daftar "Project", mengikuti gaya Category Pills di
 * "Cari Project" (2011:274): aktif = coklat + bayangan, tidak aktif = container pasir.
 */
@Composable
internal fun ProposalFilterRow(
    options: List<ProposalFilterOption>,
    selectedFilter: ProjectFilter,
    onFilterSelect: (ProjectFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    // Figma "Filter Pills" (449:866): px 16, gap 7.6, tinggi pill 29.6 (hug).
    // contentPadding vertikal 4dp = ruang bayangan pill aktif (Figma memotong 4dp di bawah).
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(7.58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = options, key = { it.filter.name }) { option ->
            FilterPill(
                text = "${option.filter.label} (${option.count})",
                selected = option.filter == selectedFilter,
                onClick = { onFilterSelect(option.filter) }
            )
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = TemuKaryaTypography.LabelSemiBold.withFigmaLineHeight(),
        color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody,
        maxLines = 1,
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier.dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 4.226.dp,
                            spread = 0.dp,
                            color = TemuKaryaColors.ShadowPill,
                            offset = DpOffset(0.dp, 2.113.dp)
                        )
                    )
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(
                if (selected) TemuKaryaColors.ActionPrimary else TemuKaryaColors.SurfaceContainer
            )
            .selectable(selected = selected, onClick = onClick, role = Role.Tab)
            .padding(horizontal = 14.79.dp, vertical = 6.338.dp)
    )
}
