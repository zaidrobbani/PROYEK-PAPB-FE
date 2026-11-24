package com.example.proyek_papb_fe.core.data.model

import androidx.annotation.DrawableRes

enum class ProjectCategory(val label: String) {
    PACKAGING("Desain Kemasan"),
    BRANDING("Branding & Logo"),
    PRODUCT_PHOTO("Foto Produk"),
    WEB_STORE("Web Toko Online"),
    SOCIAL_MEDIA("Media Sosial")
}

enum class MatchLevel { TOP, GOOD }

data class ProjectMatch(
    val percentage: Int,
    val level: MatchLevel
)

enum class AvatarTone { GREEN, SAND }

data class FreelancerProject(
    val id: String,
    val title: String,
    val category: ProjectCategory,
    val tagLabel: String,
    val description: String,
    val skills: List<String>,
    val publishedAgo: String,

    val clientName: String,
    val clientInitials: String,
    val avatarTone: AvatarTone,
    val isClientVerified: Boolean = true,
    val clientLocation: String,
    val clientLocationFull: String,
    val ownerName: String,
    val ownerRole: String,
    val clientRating: Double,
    /** Foto profil UMKM dari BE (`profile_image_url`), null = avatar inisial. */
    val clientPhotoUrl: String? = null,

    val budgetLabel: String,
    val budget: String,
    val budgetCompact: String,
    val deadlineLabel: String,
    val deadline: String,
    val deadlineCompact: String,
    val applicationDaysLeft: Int,
    val otherApplicantCount: Int,

    val match: ProjectMatch? = null,
    val isNew: Boolean = false,
    @param:DrawableRes val imageRes: Int? = null
)
