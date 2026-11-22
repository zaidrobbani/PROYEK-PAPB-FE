package com.example.proyek_papb_fe.core.domain.model

data class FreelancerSummary(
    val userId: String,
    val name: String,
    val professionalTitle: String,
    val city: String,
    val isAvailable: Boolean,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val skills: List<Skill>,
    val experienceCount: Int,
    val averageRating: Double?,
    val reviewCount: Int
)

data class FreelancerDetail(
    val summary: FreelancerSummary,
    val bio: String?,
    val experiences: List<Experience>,
    val portfolios: List<Portfolio>
)

data class FreelancerSearchFilter(
    val search: String? = null,
    val skillIds: List<String> = emptyList(),
    val city: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val minRating: Double? = null
)
