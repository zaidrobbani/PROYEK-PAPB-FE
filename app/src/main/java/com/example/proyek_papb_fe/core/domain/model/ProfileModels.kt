package com.example.proyek_papb_fe.core.domain.model

data class UmkmBusinessProfile(
    val userId: String,
    val businessName: String,
    val businessCategory: String,
    val businessAddress: String,
    val city: String
)

object AvailabilityStatus {
    const val AVAILABLE = "AVAILABLE"
    const val BUSY = "BUSY"
}

data class FreelancerProfessionalProfile(
    val userId: String,
    val professionalTitle: String,
    val city: String,
    val bio: String?,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val availabilityStatus: String
) {
    val isAvailable: Boolean get() = availabilityStatus == AvailabilityStatus.AVAILABLE
}

data class Experience(
    val id: String,
    val jobTitle: String,
    val organizationName: String,
    val description: String,
    /** Format `yyyy-MM-dd`. */
    val startDate: String,
    val endDate: String?
)

data class Portfolio(
    val id: String,
    val title: String,
    val description: String,
    val projectUrl: String?,
    val updatedAtMillis: Long?,
    val coverImageUrl: String? = null
)

data class PortfolioMedia(
    val id: String,
    val portfolioId: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sortOrder: Int,
    val isCover: Boolean
)
