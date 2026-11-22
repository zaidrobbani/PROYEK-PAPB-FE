package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.core.data.model.PortfolioCategory
import com.example.proyek_papb_fe.core.data.model.PortfolioItem
import com.example.proyek_papb_fe.core.data.model.WorkExperience

/**
 * Cache tampilan profil freelancer yang sedang login, diisi `FreelancerProfileViewModel` dari BE.
 * Kategori & skill portofolio serta jenis pekerjaan pengalaman belum ada di BE, jadi disimpan lokal per id.
 */
object FreelancerProfileStore {

    private val EmptyProfile = FreelancerProfile(
        fullName = "",
        initials = "",
        headline = "",
        bio = "",
        city = "",
        primaryField = "",
        startingRate = 0,
        rating = 0.0,
        reviewCount = 0,
        isAvailableForWork = true,
        acceptsRemoteWork = true,
        completedProjects = 0,
        trustPercent = 0,
        experienceYearsLabel = "0 thn",
        skills = emptyList()
    )


    var profile: FreelancerProfile by mutableStateOf(EmptyProfile)
        private set

    var portfolio: List<PortfolioItem> by mutableStateOf(emptyList())
        private set

    var experiences: List<WorkExperience> by mutableStateOf(emptyList())
        private set

    /** false bila freelancer belum membuat profil profesional (wajib sebelum melamar). */
    var hasProfile: Boolean by mutableStateOf(false)
        private set

    internal val portfolioExtras = mutableMapOf<String, Pair<PortfolioCategory, List<String>>>()
    internal val experienceTypes = mutableMapOf<String, com.example.proyek_papb_fe.core.data.model.EmploymentType>()

    fun replace(profile: FreelancerProfile, hasProfile: Boolean) {
        this.profile = profile
        this.hasProfile = hasProfile
    }

    fun replacePortfolio(items: List<PortfolioItem>) {
        portfolio = items
    }

    fun replaceExperiences(items: List<WorkExperience>) {
        experiences = items.sortedByDescending { it.start }
    }

    fun portfolioItem(id: String): PortfolioItem? = portfolio.firstOrNull { it.id == id }

    fun experience(id: String): WorkExperience? = experiences.firstOrNull { it.id == id }

    fun clear() {
        profile = EmptyProfile
        portfolio = emptyList()
        experiences = emptyList()
        hasProfile = false
        portfolioExtras.clear()
        experienceTypes.clear()
    }
}
