package com.example.proyek_papb_fe.feature.umkm.dashboard.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmBottomNav
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTopBar
import com.example.proyek_papb_fe.feature.umkm.dashboard.component.ActiveProjectCard
import com.example.proyek_papb_fe.feature.umkm.dashboard.component.CreateProjectBanner
import com.example.proyek_papb_fe.feature.umkm.dashboard.component.DashboardSearchBar
import com.example.proyek_papb_fe.feature.umkm.dashboard.component.DashboardSectionHeader
import com.example.proyek_papb_fe.feature.umkm.dashboard.component.FreelancerRecommendationCard

/** Jarak horizontal halaman pada frame Figma. */
private val PageGutter = 16.903.dp

/** Setiap bagian: jarak antarbagian 28 ditambah margin atas 21.128 dari frame Figma. */
private val SectionTopMargin = 21.128.dp

@Composable
fun UmkmDashboardScreen(
    onNotificationsClick: () -> Unit,
    onSearch: (query: String) -> Unit,
    onFilterClick: () -> Unit,
    onCreateProject: () -> Unit,
    onSeeAllProjects: () -> Unit,
    onProjectClick: (projectId: String) -> Unit,
    onSeeAllFreelancers: () -> Unit,
    onFreelancerClick: (freelancerId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier,
    data: UmkmDashboardData = DummyUmkmDashboard
) {
    var query by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        UmkmTopBar(onNotificationsClick = onNotificationsClick)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 15.774.dp, bottom = 29.415.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "Halo, ${data.ownerFirstName}",
                modifier = Modifier.padding(start = PageGutter, end = PageGutter, top = 4.226.dp),
                color = colors.onSurface,
                style = MaterialTheme.typography.headlineSmall.withFrameMetrics(
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.241.sp,
                    lineHeight = 29.579.sp,
                    letterSpacing = (-0.581).sp
                )
            )
            DashboardSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    // Kata kunci dibawa ke Cari Freelancer; kolom di Beranda dikosongkan lagi.
                    val keyword = query.trim()
                    query = ""
                    onSearch(keyword)
                },
                onFilterClick = onFilterClick,
                modifier = Modifier.padding(start = PageGutter, end = PageGutter, top = SectionTopMargin)
            )
            CreateProjectBanner(
                onCreateProject = onCreateProject,
                modifier = Modifier.padding(start = PageGutter, end = PageGutter, top = SectionTopMargin)
            )
            Column(
                modifier = Modifier.padding(
                    start = PageGutter,
                    end = PageGutter,
                    top = SectionTopMargin + 4.226.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.677.dp)
            ) {
                DashboardSectionHeader(
                    title = "Project Aktif Saya",
                    actionLabel = "Semua Riwayat",
                    actionColor = colors.secondary,
                    onActionClick = onSeeAllProjects
                )
                data.activeProjects.forEach { project ->
                    ActiveProjectCard(
                        project = project,
                        onClick = { onProjectClick(project.id) }
                    )
                }
            }
            Column(
                modifier = Modifier.padding(top = SectionTopMargin),
                verticalArrangement = Arrangement.spacedBy(12.677.dp)
            ) {
                DashboardSectionHeader(
                    title = "Rekomendasi Freelancer",
                    actionLabel = "Lihat Semua",
                    actionColor = colors.onTertiaryContainer,
                    onActionClick = onSeeAllFreelancers,
                    modifier = Modifier.padding(horizontal = PageGutter)
                )
                // Carousel selebar layar; ruang bawah 4.23 untuk bayangan kartu seperti di Figma.
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(207.056.dp),
                    contentPadding = PaddingValues(
                        start = PageGutter,
                        end = PageGutter,
                        bottom = 4.226.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.677.dp)
                ) {
                    items(data.recommendedFreelancers, key = { it.id }) { freelancer ->
                        FreelancerRecommendationCard(
                            freelancer = freelancer,
                            onClick = { onFreelancerClick(freelancer.id) },
                            modifier = Modifier.height(202.831.dp)
                        )
                    }
                }
            }
        }
        UmkmBottomNav(
            selectedTab = UmkmTab.Beranda,
            onTabSelected = { tab -> if (tab != UmkmTab.Beranda) onTabSelected(tab) }
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun UmkmDashboardScreenPreview() {
    PROYEKPAPBFETheme {
        UmkmDashboardScreen(
            onNotificationsClick = {},
            onSearch = {},
            onFilterClick = {},
            onCreateProject = {},
            onSeeAllProjects = {},
            onProjectClick = {},
            onSeeAllFreelancers = {},
            onFreelancerClick = {},
            onTabSelected = {}
        )
    }
}
