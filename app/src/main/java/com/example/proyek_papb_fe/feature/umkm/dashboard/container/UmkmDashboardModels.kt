package com.example.proyek_papb_fe.feature.umkm.dashboard.container

import androidx.annotation.DrawableRes

data class DashboardActiveProject(
    val id: String,
    val title: String,
    val statusLabel: String,
    val freelancerName: String,
    @param:DrawableRes val freelancerPhoto: Int? = null,
    val budget: String,
    val progress: Float
)

data class DashboardFreelancer(
    val id: String,
    val name: String,
    val role: String,
    @param:DrawableRes val photo: Int? = null,
    val rating: String,
    val reviewLabel: String,
    val startingPrice: String
)

data class UmkmDashboardData(
    val ownerFirstName: String,
    val activeProjects: List<DashboardActiveProject>,
    val recommendedFreelancers: List<DashboardFreelancer>
)
