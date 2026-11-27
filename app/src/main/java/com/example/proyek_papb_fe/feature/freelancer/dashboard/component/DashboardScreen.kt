package com.example.proyek_papb_fe.feature.freelancer.dashboard.component

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import kotlinx.coroutines.Job
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R

data class DashboardProject(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String,
    val budget: String,
    val status: String,
    val progress: Int = 0,
    val owner: String = "",
    val daysRemaining: Int? = null,
    val matchPercentage: Int? = null,
    val imageEmoji: String = "🖼️",
    @param:DrawableRes val imageRes: Int? = null
)

private val ScreenHorizontalPadding = 16.903.dp

@Composable
fun DashboardScreen(
    userName: String,
    availableBalance: String,
    activeProject: DashboardProject?,
    recommendations: List<DashboardProject>,
    onWalletClick: () -> Unit,
    onActiveProjectClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Job)? = null,
    hasUnreadNotification: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onSeeAllActiveProjectsClick: () -> Unit = {},
    onSeeAllRecommendationsClick: () -> Unit = {},
    hasSkills: Boolean? = null,
    isLoading: Boolean = false,
    onManageSkillsClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DashboardColors.BgCanvas)
    ) {
        DashboardTopBar(
            hasUnreadNotification = hasUnreadNotification,
            onNotificationClick = onNotificationClick
        )

        RefreshableContent(onRefresh = onRefresh, modifier = Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 15.774.dp, bottom = 25.354.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item(key = "greeting") {
                    Text(
                        text = "Halo, $userName",
                        style = DashboardTypography.Greeting,
                        color = DashboardColors.TextHeading,
                        modifier = Modifier.padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                            top = 4.226.dp
                        )
                    )
                }

                item(key = "wallet") {
                    WalletSummaryCard(
                        balance = availableBalance,
                        onWithdrawClick = onWalletClick,
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding)
                    )
                }

                item(key = "active_project") {
                    Column(
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(8.451.dp)
                    ) {
                        SectionHeader(
                            title = "Proyek Aktif Saya",
                            onSeeAllClick = onSeeAllActiveProjectsClick
                        )
                        if (activeProject != null) {
                            ActiveProjectCard(
                                project = activeProject,
                                onClick = { onActiveProjectClick(activeProject.id) }
                            )
                        } else {
                            Text(
                                text = "Belum ada proyek aktif.",
                                style = DashboardTypography.BodyRegular,
                                color = DashboardColors.TextBody
                            )
                        }
                    }
                }

                item(key = "recommendations") {
                    RecommendationSection(
                        recommendations = recommendations,
                        onProjectClick = onProjectClick,
                        onSeeAllClick = onSeeAllRecommendationsClick,
                        hasSkills = hasSkills,
                        isLoading = isLoading,
                        onManageSkillsClick = onManageSkillsClick
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationSection(
    recommendations: List<DashboardProject>,
    onProjectClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    hasSkills: Boolean?,
    isLoading: Boolean,
    onManageSkillsClick: () -> Unit
) {
    if (recommendations.isEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
            SectionHeader(
                title = "Rekomendasi Proyek",
                onSeeAllClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = 17.dp + ScreenHorizontalPadding)
            )
            if (!isLoading) {
                EmptyRecommendations(
                    hasSkills = hasSkills,
                    onManageSkillsClick = onManageSkillsClick,
                    onSearchClick = onSeeAllClick,
                    modifier = Modifier.padding(horizontal = 17.dp + ScreenHorizontalPadding)
                )
            }
        }
        return
    }
    val listState = rememberLazyListState()
    Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
        SectionHeader(
            title = "Rekomendasi Proyek",
            onSeeAllClick = onSeeAllClick,
            modifier = Modifier.padding(horizontal = 17.dp + ScreenHorizontalPadding)
        )
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(
                start = 17.dp,
                end = 17.dp,
                top = 4.23.dp,
                bottom = 12.68.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.677.dp)
        ) {
            items(items = recommendations, key = { it.id }) { project ->
                RecommendationCard(
                    project = project,
                    onClick = { onProjectClick(project.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 851)
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        userName = "Andi",
        availableBalance = "Rp 2.850.000",
        activeProject = DashboardProject(
            id = "1",
            title = "Desain Rebranding Logo & Kemasan Pouch",
            category = "Desain Grafis",
            budget = "Rp 1.500.000",
            status = "Sedang Dikerjakan",
            progress = 20,
            owner = "Kedai Kopi Senja (Rina Pratiwi, Bandung)",
            daysRemaining = 7
        ),
        recommendations = listOf(
            DashboardProject(
                id = "2",
                title = "Rebranding Kemasan Keripik Singkong Renyah",
                category = "Desain Grafis",
                budget = "Rp 1.800.000",
                status = "Terbuka",
                matchPercentage = 96,
                imageRes = R.drawable.img_reco_keripik
            ),
            DashboardProject(
                id = "3",
                title = "Desain Menu Digital & Banner Promosi Cafe",
                category = "Desain Grafis",
                budget = "Rp 1.200.000",
                status = "Terbuka",
                matchPercentage = 92,
                imageRes = R.drawable.img_reco_cafe
            )
        ),
        onWalletClick = {},
        onActiveProjectClick = {},
        onProjectClick = {}
    )
}

@Composable
private fun EmptyRecommendations(
    hasSkills: Boolean?,
    onManageSkillsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noSkills = hasSkills == false
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DashboardColors.SurfaceSand.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (noSkills) "Tambahkan skill-mu dulu" else "Belum ada proyek yang cocok",
            style = DashboardTypography.CardTitle,
            color = DashboardColors.TextHeading
        )
        Text(
            text = if (noSkills) {
                "Rekomendasi dihitung dari kecocokan skill-mu dengan skill yang dibutuhkan proyek (minimal 60%). " +
                    "Atur skill di Profil Profesional agar proyek yang cocok muncul di sini."
            } else {
                "Belum ada proyek terbuka yang minimal 60% skill-nya sesuai denganmu. " +
                    "Tambah skill lain atau cari proyek secara manual."
            },
            style = DashboardTypography.BodyRegular,
            color = DashboardColors.TextBody
        )
        Button(
            onClick = if (noSkills) onManageSkillsClick else onSearchClick,
            colors = ButtonDefaults.buttonColors(containerColor = DashboardColors.ActionPrimary)
        ) {
            Text(if (noSkills) "Atur Skill" else "Cari Proyek", style = DashboardTypography.LabelSemiBold)
        }
    }
}
