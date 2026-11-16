package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
 * Pill filter status tab Chat, gaya sama dengan pill "Cari Project" / "Project":
 * aktif = cokelat + bayangan, tidak aktif = container pasir. Melebar sampai tepi layar.
 */
@Composable
internal fun ChatFilterRow(
    options: List<ChatFilterOption>,
    selectedFilter: ProjectFilter,
    onFilterSelect: (ProjectFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .selectableGroup(),
        contentPadding = PaddingValues(horizontal = ChatHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(7.6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = options, key = { it.filter.name }) { option ->
            val selected = option.filter == selectedFilter
            Text(
                text = "${option.filter.label} (${option.count})",
                style = TemuKaryaTypography.LabelSemiBold,
                color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody,
                maxLines = 1,
                modifier = Modifier
                    .then(
                        if (selected) {
                            Modifier.dropShadow(
                                shape = CircleShape,
                                shadow = Shadow(
                                    radius = 4.2.dp,
                                    spread = 0.dp,
                                    color = TemuKaryaColors.ShadowPill,
                                    offset = DpOffset(0.dp, 2.1.dp)
                                )
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(CircleShape)
                    .background(if (selected) TemuKaryaColors.ActionPrimary else TemuKaryaColors.SurfaceContainer)
                    .selectable(selected = selected, onClick = { onFilterSelect(option.filter) }, role = Role.Tab)
                    .padding(horizontal = 14.8.dp, vertical = 6.3.dp)
            )
        }
    }
}
