package com.example.proyek_papb_fe.feature.umkm.find_freelancer.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.QuickFilter

/**
 * Baris chip: kategori (pilih satu, "Semua Kategori" = tanpa kategori) lalu filter cepat
 * (bisa lebih dari satu). Gaya aktif/tidak aktif mengikuti chip di Figma.
 */
@Composable
fun FindFreelancerFilters(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    activeQuickFilters: Set<QuickFilter>,
    onQuickFilterToggle: (QuickFilter) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item(key = "all") {
            FilterChip(
                label = "Semua Kategori",
                // Figma: "Semua Kategori" tidak aktif saat kategori lain dipilih.
                background = if (selectedCategory == null) colors.primary else colors.surfaceContainerHigh,
                contentColor = if (selectedCategory == null) colors.onPrimary else colors.onSurface,
                shadow = selectedCategory == null,
                trailingCheck = if (selectedCategory == null) R.drawable.ic_find_check_white else null,
                modifier = Modifier.selectable(
                    selected = selectedCategory == null,
                    role = Role.RadioButton,
                    onClick = { onCategorySelect(null) }
                )
            )
        }
        categories.forEach { category ->
            item(key = "category-$category") {
                val selected = category == selectedCategory
                FilterChip(
                    label = category,
                    background = if (selected) colors.primary else colors.tertiaryContainer,
                    contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                    shadow = selected,
                    trailingCheck = if (selected) R.drawable.ic_find_check_white else null,
                    modifier = Modifier.selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = { onCategorySelect(if (selected) null else category) }
                    )
                )
            }
        }
        QuickFilter.entries.forEach { filter ->
            item(key = "quick-${filter.name}") {
                val active = filter in activeQuickFilters
                FilterChip(
                    label = filter.label,
                    background = if (active) SageBadge else colors.tertiaryContainer,
                    contentColor = if (active) OnSageBadge else colors.onSurfaceVariant,
                    shadow = false,
                    leadingStar = active && filter == QuickFilter.Rating48,
                    trailingCheck = if (active) R.drawable.ic_register_role_check else null,
                    modifier = Modifier.toggleable(
                        value = active,
                        role = Role.Checkbox,
                        onValueChange = { onQuickFilterToggle(filter) }
                    )
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    background: Color,
    contentColor: Color,
    shadow: Boolean,
    trailingCheck: Int?,
    modifier: Modifier,
    leadingStar: Boolean = false
) {
    Row(
        modifier = Modifier
            .then(if (shadow) Modifier.authDropShadow(CircleShape, AuthShadowStyle.Subtle) else Modifier)
            .clip(CircleShape)
            .background(background)
            .then(modifier)
            .padding(horizontal = 12.677.dp, vertical = 6.338.dp),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingStar) {
            Image(painter = painterResource(R.drawable.ic_find_star_chip), contentDescription = null)
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.677.sp,
                lineHeight = 16.903.sp
            )
        )
        trailingCheck?.let { Image(painter = painterResource(it), contentDescription = null) }
    }
}

/** "18 freelancer sesuai filter" dengan label "Sesuai Kriteria". */
@Composable
fun FindFreelancerResultHeader(count: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.226.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_find_result), contentDescription = null)
            Text(
                text = "$count freelancer sesuai filter",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
        Text(
            text = "Sesuai Kriteria",
            modifier = Modifier
                .background(SageBadge.copy(alpha = 0.5f), CircleShape)
                .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
            color = colors.secondary,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}
