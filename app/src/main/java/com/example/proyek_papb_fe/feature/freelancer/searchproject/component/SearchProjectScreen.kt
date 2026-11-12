package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.ui.components.RootTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography
import kotlinx.coroutines.launch

internal val SearchProjectHorizontalPadding = 16.903.dp

/** Figma: Cari Project (2011:255). Header pencarian tetap, hanya feed yang di-scroll. */
@Composable
fun SearchProjectScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<CategoryFilterOption>,
    selectedCategory: ProjectCategory?,
    onCategorySelect: (ProjectCategory?) -> Unit,
    projects: List<SearchProject>,
    onProjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeFilterCount: Int = 0,
    onFilterClick: () -> Unit = {},
    hasUnreadNotification: Boolean = false,
    onNotificationClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        RootTopBar(
            title = "Cari Project",
            hasUnreadNotification = hasUnreadNotification,
            onNotificationClick = onNotificationClick
        )

        SearchFilterHeader(
            query = query,
            onQueryChange = { value ->
                onQueryChange(value)
                scope.launch { listState.scrollToItem(0) }
            },
            activeFilterCount = activeFilterCount,
            onFilterClick = onFilterClick,
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelect = { category ->
                onCategorySelect(category)
                scope.launch { listState.scrollToItem(0) }
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                horizontal = SearchProjectHorizontalPadding,
                vertical = 8.451.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (projects.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "Tidak ada project yang cocok dengan pencarian Anda.",
                        style = TemuKaryaTypography.BodyRegular,
                        color = TemuKaryaColors.TextBody,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    )
                }
            }
            items(items = projects, key = { it.id }) { project ->
                ProjectFeedCard(
                    project = project,
                    onClick = { onProjectClick(project.id) }
                )
            }
        }
    }
}
