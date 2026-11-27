package com.example.proyek_papb_fe.core.domain.model

object ProjectStatusCode {
    const val TERBUKA = "TERBUKA"
    const val MENUNGGU_KESEPAKATAN = "MENUNGGU_KESEPAKATAN"
    const val MENUNGGU_PEMBAYARAN = "MENUNGGU_PEMBAYARAN"
    const val SIAP_DIKERJAKAN = "SIAP_DIKERJAKAN"
    const val SEDANG_DIKERJAKAN = "SEDANG_DIKERJAKAN"
    const val MENUNGGU_REVIEW = "MENUNGGU_REVIEW"
    const val REVISI = "REVISI"
    const val SELESAI = "SELESAI"
    const val DIBATALKAN = "DIBATALKAN"
}

data class Project(
    val id: String,
    val umkmId: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val budget: Long,
    val deadline: String,
    val deadlineMillis: Long?,
    val status: String,
    val publishedAtMillis: Long?,
    val createdAtMillis: Long?,
    val skillIds: List<String>? = null,
    val owner: UserSummary? = null
)

data class ProjectInput(
    val categoryId: String,
    val title: String,
    val description: String,
    val budget: Long,
    val deadline: String,
    val skillIds: List<String>
)

data class ProjectFile(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long
)

data class UploadFile(
    val uri: String,
    val fileName: String,
    val mimeType: String?
)

object ApplicationStatusCode {
    const val SUBMITTED = "SUBMITTED"
    const val ACCEPTED = "ACCEPTED"
    const val NOT_SELECTED = "NOT_SELECTED"
    const val CANCELLED = "CANCELLED"
}

data class Application(
    val id: String,
    val projectId: String,
    val freelancerId: String,
    val proposedPrice: Long,
    val durationDays: Int,
    val message: String,
    val status: String,
    val submittedAtMillis: Long?
)

data class SkillMatch(
    val scorePercent: Double,
    val matchedSkillIds: List<String>,
    val missingSkillIds: List<String>,
    val meetsThreshold: Boolean
)

data class ProjectRecommendation(
    val projectId: String,
    val umkmId: String,
    val title: String,
    val categoryId: String,
    val status: String,
    val requiredSkillIds: List<String>,
    val match: SkillMatch
)

data class FreelancerRecommendation(
    val freelancerId: String,
    val name: String,
    val professionalTitle: String,
    val city: String,
    val skillIds: List<String>,
    val servicePriceMin: Long?,
    val servicePriceMax: Long?,
    val match: SkillMatch
)
