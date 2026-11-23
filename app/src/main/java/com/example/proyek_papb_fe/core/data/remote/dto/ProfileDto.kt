package com.example.proyek_papb_fe.core.data.remote.dto

data class UmkmProfileDto(
    val userId: String,
    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,
    val city: String
)

data class UmkmProfileRequest(
    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,
    val city: String
)

data class FreelancerProfileDto(
    val userId: String,
    val professionalTitle: String,
    val city: String,
    val bio: String?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val availabilityStatus: String?
)

data class FreelancerProfileRequest(
    val professionalTitle: String,
    val city: String,
    val bio: String?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    /** Wajib saat PUT, diabaikan saat POST. */
    val availabilityStatus: String? = null
)

data class FreelancerSkillsDto(val freelancerId: String, val skillIds: List<String>?)

data class SetSkillsRequest(val skillIds: List<String>)

data class ExperienceDto(
    val id: String,
    val freelancerId: String?,
    val jobTitle: String,
    val organizationName: String,
    val description: String,
    val startDate: String,
    val endDate: String?
)

data class ExperienceRequest(
    val jobTitle: String,
    val organizationName: String,
    val description: String,
    val startDate: String,
    val endDate: String?
)

data class PortfolioDto(
    val id: String,
    val freelancerId: String?,
    val title: String,
    val description: String,
    val projectUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
    /** Hanya dikirim endpoint discovery (detail freelancer); path relatif yang butuh token. */
    val coverImageUrl: String? = null
)

data class PortfolioRequest(
    val title: String,
    val description: String,
    val projectUrl: String?
)

data class PortfolioMediaDto(
    val id: String,
    val portfolioId: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?,
    val sortOrder: Int,
    val isCover: Boolean
)
