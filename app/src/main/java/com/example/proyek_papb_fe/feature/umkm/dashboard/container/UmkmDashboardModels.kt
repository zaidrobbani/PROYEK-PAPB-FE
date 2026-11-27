package com.example.proyek_papb_fe.feature.umkm.dashboard.container

import androidx.annotation.DrawableRes

data class DashboardActiveProject(
    val id: String,
    val title: String,
    val statusLabel: String,
    val freelancerName: String,
    @param:DrawableRes val freelancerPhoto: Int? = null,
    val freelancerPhotoUrl: String? = null,
    val budget: String,
    val progress: Float
)

data class DashboardFreelancer(
    val id: String,
    val name: String,
    val role: String,
    @param:DrawableRes val photo: Int? = null,
    val photoUrl: String? = null,
    val rating: String,
    val reviewLabel: String,
    val startingPrice: String,
    /** "Mulai dari" bila hanya ada harga minimum, "Kisaran harga" bila ada rentang. */
    val priceLabel: String = "Mulai dari",
    val city: String = "",
    val isAvailable: Boolean = true,
    val skills: List<String> = emptyList(),
    val experienceCount: Int = 0,
    /** Persentase kecocokan skill (≥ 60%) dengan project TERBUKA milik UMKM; null bila tidak ada. */
    val matchPercent: Int? = null,
    val matchProjectTitle: String? = null
)

data class UmkmDashboardData(
    val ownerFirstName: String,
    val activeProjects: List<DashboardActiveProject>,
    val recommendedFreelancers: List<DashboardFreelancer>
)
