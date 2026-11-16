package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Category Pills Scrollable (2011:274), melebar sampai tepi layar. */
@Composable
internal fun CategoryPillRow(
    categories: List<CategoryFilterOption>,
    selectedCategory: ProjectCategory?,
    onCategorySelect: (ProjectCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(38.031.dp)
            .selectableGroup(),
        contentPadding = PaddingValues(horizontal = SearchProjectHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(7.58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = categories, key = { it.category?.name ?: "ALL" }) { option ->
            CategoryPill(
                text = "${option.label} (${option.count})",
                selected = option.category == selectedCategory,
                onClick = { onCategorySelect(option.category) }
            )
        }
    }
}

@Composable
private fun CategoryPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = TemuKaryaTypography.LabelSemiBold,
        color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody,
        maxLines = 1,
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier.searchProjectShadow(
                        shape = CircleShape,
                        color = TemuKaryaColors.ShadowPill,
                        radius = 4.226.dp,
                        spread = 0.dp,
                        offsetY = 2.113.dp
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
