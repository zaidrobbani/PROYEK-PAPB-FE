package com.example.proyek_papb_fe.feature.umkm.dashboard.container

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount

/*
 * DATA DUMMY untuk simulasi frontend, diambil dari contoh isi frame Figma Beranda UMKM.
 * Bukan data dari backend; ganti dengan data API setelah integrasi tersedia.
 */

data class DashboardActiveProject(
    val id: String,
    val title: String,
    /** Label status sesuai daftar status project di Blueprint. */
    val statusLabel: String,
    val freelancerName: String,
    @param:DrawableRes val freelancerPhoto: Int,
    val budget: String,
    /** 0f sampai 1f. */
    val progress: Float
)

data class DashboardFreelancer(
    val id: String,
    val name: String,
    val role: String,
    @param:DrawableRes val photo: Int,
    val rating: String,
    val reviewLabel: String,
    val startingPrice: String
)

data class UmkmDashboardData(
    val ownerFirstName: String,
    val activeProjects: List<DashboardActiveProject>,
    val recommendedFreelancers: List<DashboardFreelancer>
)

/** Getter agar sapaan "Halo, …" ikut berubah setelah Kelola Profil Usaha disimpan. */
internal val DummyUmkmDashboard: UmkmDashboardData
    get() = UmkmDashboardData(
    ownerFirstName = UmkmAccount.profile.firstName,
    activeProjects = listOf(
        DashboardActiveProject(
            id = "dummy-project-1",
            title = "Desain Kemasan Keripik Tempe",
            statusLabel = "Sedang Dikerjakan",
            freelancerName = "Andi Ramadhan",
            freelancerPhoto = R.drawable.img_dashboard_project_freelancer,
            budget = "Rp 1.500.000",
            progress = 0.2f
        )
    ),
    recommendedFreelancers = listOf(
        DashboardFreelancer(
            id = "dummy-freelancer-andi",
            name = "Andi Ramadhan",
            role = "Desainer Grafis & Branding",
            photo = R.drawable.img_dashboard_freelancer_andi,
            rating = "4.9",
            reviewLabel = "(48 ulasan)",
            startingPrice = "Rp 500.000"
        ),
        DashboardFreelancer(
            id = "dummy-freelancer-maya",
            name = "Maya Safitri",
            role = "Fotografer & Video Kreator",
            photo = R.drawable.img_dashboard_freelancer_maya,
            rating = "5.0",
            reviewLabel = "32 review",
            startingPrice = "Rp 750.000"
        )
    )
)
