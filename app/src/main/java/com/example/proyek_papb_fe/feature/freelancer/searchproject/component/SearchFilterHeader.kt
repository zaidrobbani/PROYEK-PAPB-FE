package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val ControlHeight = 50.708.dp
private val ControlShape = RoundedCornerShape(12.dp)

/**
 * Figma: Interactive Sticky Search & Filter Header (2011:258).
 * Diletakkan di luar daftar sehingga tetap diam saat feed di-scroll.
 */
@Composable
internal fun SearchFilterHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
    categories: List<CategoryFilterOption>,
    selectedCategory: ProjectCategory?,
    onCategorySelect: (ProjectCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.BgCanvas)
            // Figma: Main pt 67.61 dikurangi app bar 64, lalu header pt 8.451.
            .padding(top = 3.61.dp + 8.451.dp, bottom = 4.226.dp),
        verticalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SearchProjectHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier.weight(1f)
            )
            FilterButton(
                activeFilterCount = activeFilterCount,
                onClick = onFilterClick
            )
        }

        CategoryPillRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelect = onCategorySelect
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val placeholder = "Cari project desain, web, branding..."

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.semantics { contentDescription = "Cari project" },
        textStyle = TemuKaryaTypography.Input.copy(color = TemuKaryaColors.TextPrimary),
        singleLine = true,
        cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ControlHeight)
                    .searchProjectShadow(ControlShape, TemuKaryaColors.ShadowSoft)
                    .clip(ControlShape)
                    .background(TemuKaryaColors.SurfaceWhite)
                    .padding(horizontal = 12.677.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Aset Figma sudah termasuk margin kanan ikon (24.297 x 15.846).
                Image(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TemuKaryaTypography.Input,
                            color = TemuKaryaColors.TextPlaceholder,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

/** Figma: Button - Buka Filter (2011:267) dengan badge jumlah filter aktif. */
@Composable
private fun FilterButton(
    activeFilterCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(ControlHeight)
            .searchProjectShadow(ControlShape, TemuKaryaColors.ShadowSoft)
            .clip(ControlShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .clickable(onClickLabel = "Buka filter", onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_tune),
            contentDescription = "Buka filter"
        )
        if (activeFilterCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.45.dp, end = 8.45.dp)
                    .size(16.903.dp)
                    .clip(CircleShape)
                    .background(TemuKaryaColors.ActionPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeFilterCount.toString(),
                    style = TemuKaryaTypography.Counter,
                    color = TemuKaryaColors.OnAction,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
