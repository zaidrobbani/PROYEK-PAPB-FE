package com.example.proyek_papb_fe.core.data.remote.dto

data class FreelancerSummaryDto(
    val userId: String,
    val name: String,
    val profileImageUrl: String?,
    val professionalTitle: String,
    val city: String,
    val availabilityStatus: String?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val skills: List<SkillDto>?,
    val experienceCount: Int?,
    val averageRating: Double?,
    val reviewCount: Int?
)

data class FreelancerDetailDto(
    val userId: String,
    val name: String,
    val profileImageUrl: String?,
    val professionalTitle: String,
    val city: String,
    val availabilityStatus: String?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val skills: List<SkillDto>?,
    val experienceCount: Int?,
    val averageRating: Double?,
    val reviewCount: Int?,
    val bio: String?,
    val experiences: List<ExperienceDto>?,
    val portfolios: List<PortfolioDto>?,
    val reviews: List<FreelancerReviewDto>?
)

data class FreelancerReviewDto(
    val id: String,
    val projectId: String,
    val rating: Int,
    val reviewText: String?,
    val createdAt: String?
)

data class SkillMatchDto(
    val scorePercent: Double,
    val requiredCount: Int,
    val matchedCount: Int,
    val matchedSkillIds: List<String>?,
    val missingSkillIds: List<String>?,
    val meetsThreshold: Boolean
)

data class FreelancerMatchSourceDto(
    val freelancerId: String,
    val name: String,
    val professionalTitle: String,
    val city: String,
    val availabilityStatus: String?,
    val skillIds: List<String>?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?
)

data class FreelancerRecommendationDto(
    val freelancer: FreelancerMatchSourceDto,
    val match: SkillMatchDto
)

data class FreelancerRecommendationPageDto(
    val projectId: String,
    val items: List<FreelancerRecommendationDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)

data class ProjectMatchSourceDto(
    val projectId: String,
    val umkmId: String,
    val title: String,
    val categoryId: String,
    val status: String,
    val requiredSkillIds: List<String>?
)

data class ProjectRecommendationDto(
    val project: ProjectMatchSourceDto,
    val match: SkillMatchDto
)

data class ProjectRecommendationPageDto(
    val freelancerId: String,
    val items: List<ProjectRecommendationDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)
