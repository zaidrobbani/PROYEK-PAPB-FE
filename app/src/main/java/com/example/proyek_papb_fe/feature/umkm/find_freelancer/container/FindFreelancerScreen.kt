package com.example.proyek_papb_fe.feature.umkm.find_freelancer.container

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmBottomNav
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTopBar
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.viewmodel.FindFreelancerViewModel
import com.example.proyek_papb_fe.feature.umkm.data.QuickFilter
import com.example.proyek_papb_fe.feature.umkm.data.TalentFilter
import com.example.proyek_papb_fe.feature.umkm.data.TalentLocation
import com.example.proyek_papb_fe.feature.umkm.data.searchFreelancers
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.component.FindFreelancerFilters
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.component.FindFreelancerResultHeader
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.component.FindFreelancerSearchBar
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.component.FreelancerResultCard
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.component.TalentFilterOverlay

private val PageGutter = 16.903.dp

private const val QuickRating = 4.8

private val TalentFilterSaver = listSaver<TalentFilter, Any>(
    save = { listOf(it.minRating ?: -1.0, it.budgetMinIndex, it.budgetMaxIndex, it.location.name, it.fastResponse) },
    restore = {
        TalentFilter(
            minRating = (it[0] as Double).takeIf { rating -> rating >= 0 },
            budgetMinIndex = it[1] as Int,
            budgetMaxIndex = it[2] as Int,
            location = TalentLocation.valueOf(it[3] as String),
            fastResponse = it[4] as Boolean
        )
    }
)

@Composable
fun FindFreelancerScreen(
    initialQuery: String,
    onNotificationsClick: () -> Unit,
    onViewProfile: (freelancerId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier,
    initialShowFilter: Boolean = false,
    viewModel: FindFreelancerViewModel = viewModel(factory = FindFreelancerViewModel.factory(AppContainer.freelancerRepository, AppContainer.freelancerMatchRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var quickFilterNames by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val quickFilters = quickFilterNames.map { QuickFilter.valueOf(it) }.toSet()
    var talentFilter by rememberSaveable(stateSaver = TalentFilterSaver) { mutableStateOf(TalentFilter()) }
    var showFilter by rememberSaveable { mutableStateOf(initialShowFilter) }

    fun resultsFor(filter: TalentFilter) =
        searchFreelancers(uiState.freelancers, query, category, quickFilters, filter)

    val results = resultsFor(talentFilter)
    val activeFilterCount = (if (category != null) 1 else 0) + quickFilters.size + talentFilter.activeCount
    val chipFilters = quickFilters + listOfNotNull(QuickFilter.Rating48.takeIf { talentFilter.minRating == QuickRating })

    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UmkmTopBar(onNotificationsClick = onNotificationsClick, title = "Cari Freelancer")
        RefreshableContent(onRefresh = viewModel::refresh, modifier = Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().imePadding(),
                contentPadding = PaddingValues(top = 22.549.dp + 4.226.dp, bottom = 25.354.dp)
            ) {
                item(key = "search") {
                    FindFreelancerSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        activeFilterCount = activeFilterCount,
                        onOpenFilter = { showFilter = true },
                        modifier = Modifier.padding(horizontal = PageGutter)
                    )
                }
                item(key = "filters") {
                    FindFreelancerFilters(
                        categories = uiState.freelancers.map { it.category }.filter { it.isNotBlank() }.distinct(),
                        selectedCategory = category,
                        onCategorySelect = { category = it },
                        activeQuickFilters = chipFilters,
                        onQuickFilterToggle = { filter ->
                            if (filter == QuickFilter.Rating48) {
                                talentFilter = talentFilter.copy(
                                    minRating = if (talentFilter.minRating == QuickRating) null else QuickRating
                                )
                            } else quickFilterNames = if (filter in quickFilters) {
                                quickFilterNames - filter.name
                            } else {
                                quickFilterNames + filter.name
                            }
                        },
                        horizontalPadding = PageGutter,
                        modifier = Modifier.padding(top = 8.451.dp, bottom = 8.451.dp)
                    )
                }
                item(key = "header") {
                    FindFreelancerResultHeader(
                        count = results.size,
                        modifier = Modifier.padding(start = PageGutter, end = PageGutter, bottom = 4.226.dp)
                    )
                }
                if (results.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = "Tidak ada freelancer yang cocok. Coba kata kunci lain atau kurangi filter.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PageGutter, vertical = 33.805.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.79.sp,
                                lineHeight = 21.128.sp
                            )
                        )
                    }
                }
                items(results, key = { it.id }) { freelancer ->
                    FreelancerResultCard(
                        freelancer = freelancer,
                        onViewProfile = { onViewProfile(freelancer.id) },
                        modifier = Modifier.padding(start = PageGutter, end = PageGutter, top = 12.677.dp)
                    )
                }
            }
        }
        UmkmBottomNav(
            selectedTab = UmkmTab.Freelancer,
            onTabSelected = { tab -> if (tab != UmkmTab.Freelancer) onTabSelected(tab) }
        )
    }
    TalentFilterOverlay(
        visible = showFilter,
        applied = talentFilter,
        resultCount = { draft -> resultsFor(draft).size },
        onApply = { draft ->
            talentFilter = draft
            showFilter = false
        },
        onDismiss = { showFilter = false }
    )
    }
}

