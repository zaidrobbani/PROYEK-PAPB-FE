package com.example.proyek_papb_fe.feature.freelancer.searchproject.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.SearchProject
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.SearchProjectScreen
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.buildCategoryOptions
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.filterProjects

/**
 * Container Cari Project: menyimpan state pencarian & kategori serta mengambil
 * data dari [FreelancerProjectDummyData] (dipakai bersama dengan Detail Project).
 * (File ini sebelumnya berisi placeholder; nama file dipertahankan.)
 */
@Composable
fun SearchProjectContainer(
    onProjectClick: (Int) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<ProjectCategory?>(null) }

    val allProjects = remember {
        FreelancerProjectDummyData.getAll().map { it.toSearchProject() }
    }
    val categories = remember(allProjects) { buildCategoryOptions(allProjects) }
    val visibleProjects = remember(allProjects, query, selectedCategory) {
        allProjects.filterProjects(query = query, category = selectedCategory)
    }

    SearchProjectScreen(
        query = query,
        onQueryChange = { query = it },
        categories = categories,
        selectedCategory = selectedCategory,
        onCategorySelect = { selectedCategory = it },
        projects = visibleProjects,
        onProjectClick = onProjectClick,
        // Dummy: filter lanjutan belum dirancang, badge mengikuti Figma.
        activeFilterCount = 2,
        onFilterClick = onFilterClick,
        hasUnreadNotification = true,
        onNotificationClick = onNotificationClick
    )
}

private fun FreelancerProject.toSearchProject() = SearchProject(
    id = id,
    title = title,
    category = category,
    tagLabel = tagLabel,
    clientName = clientName,
    clientInitials = clientInitials,
    clientLocation = clientLocation,
    budgetLabel = budgetLabel,
    budget = budget,
    deadlineLabel = deadlineLabel,
    deadline = deadline,
    match = match,
    avatarTone = avatarTone,
    isNew = isNew,
    isClientVerified = isClientVerified
)

@Preview(showBackground = true, widthDp = 412, heightDp = 843)
@Composable
private fun SearchProjectContainerPreview() {
    SearchProjectContainer()
}
