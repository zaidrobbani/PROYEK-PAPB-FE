package com.example.proyek_papb_fe.feature.freelancer.searchproject.component

import com.example.proyek_papb_fe.core.data.model.AvatarTone
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.data.model.ProjectMatch

// ProjectCategory, MatchLevel, ProjectMatch & AvatarTone kini dipakai bersama di core/data/model.
// Di listing: MatchLevel.TOP = badge pasir + ikon api, GOOD = badge hijau + jempol.

/** Model tampilan kartu listing "Cari Project". */
data class SearchProject(
    val id: Int,
    val title: String,
    val category: ProjectCategory,
    /** Label tag yang tampil di kartu biasa, mis. "Website & E-Commerce". */
    val tagLabel: String,
    val clientName: String,
    val clientInitials: String,
    val clientLocation: String,
    val budgetLabel: String,
    val budget: String,
    val deadlineLabel: String,
    val deadline: String,
    /** Jika tidak null, kartu tampil sebagai kartu sorotan (latar + bayangan + badge cocok). */
    val match: ProjectMatch? = null,
    val avatarTone: AvatarTone = AvatarTone.GREEN,
    val isNew: Boolean = false,
    val isClientVerified: Boolean = true
) {
    val isHighlighted: Boolean get() = match != null
}

/** Satu pill kategori; [category] null berarti "Semua". */
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

/** Filter berdasarkan kategori lalu kata kunci (judul, klien, lokasi, kategori, tag). */
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
