package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

import com.example.proyek_papb_fe.core.data.model.AvatarTone
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.data.model.ProjectMatch

data class SearchProject(
    val id: String,
    val title: String,
    val category: ProjectCategory,
    val tagLabel: String,
    val clientName: String,
    val clientInitials: String,
    val clientLocation: String,
    val budgetLabel: String,
    val budget: String,
    val deadlineLabel: String,
    val deadline: String,
    val match: ProjectMatch? = null,
    val avatarTone: AvatarTone = AvatarTone.GREEN,
    val isNew: Boolean = false,
    val isClientVerified: Boolean = true
) {
    val isHighlighted: Boolean get() = match != null
}

data class CategoryFilterOption(
    val category: ProjectCategory?,
    val label: String,
    val count: Int
)

fun buildCategoryOptions(projects: List<SearchProject>): List<CategoryFilterOption> =
    listOf(CategoryFilterOption(category = null, label = "Semua", count = projects.size)) +
        ProjectCategory.entries.map { category ->
            CategoryFilterOption(
                category = category,
                label = category.label,
                count = projects.count { it.category == category }
            )
        }

fun List<SearchProject>.filterProjects(
    query: String,
    category: ProjectCategory?
): List<SearchProject> {
    val keyword = query.trim()
    return filter { project ->
        (category == null || project.category == category) &&
            (keyword.isEmpty() || listOf(
                project.title,
                project.clientName,
                project.clientLocation,
                project.category.label,
                project.tagLabel
            ).any { it.contains(keyword, ignoreCase = true) })
    }
}
