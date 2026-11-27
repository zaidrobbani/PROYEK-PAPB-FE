package com.example.proyek_papb_fe.core.data.remote.dto

data class ProjectDto(
    val id: String,
    val umkmId: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val budget: Long,
    val deadline: String,
    val status: String,
    val publishedAt: String?,
    val startedAt: String?,
    val completedAt: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val skillIds: List<String>? = null,
    val umkm: ProjectOwnerDto? = null
)

data class ProjectOwnerDto(
    val userId: String,
    val ownerName: String,
    val businessName: String?,
    val city: String?,
    val profileImageUrl: String?
)

data class ProjectRequest(
    val categoryId: String,
    val title: String,
    val description: String,
    val budget: Long,
    val deadline: String,
    val skillIds: List<String>
)

data class ProjectAttachmentDto(
    val id: String,
    val projectId: String,
    val originalFileName: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?
)

data class ApplicationDto(
    val id: String,
    val projectId: String,
    val freelancerId: String,
    val proposedPrice: Long,
    val estimatedDurationDays: Int,
    val proposalMessage: String,
    val status: String,
    val submittedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class SubmitApplicationRequest(
    val proposedPrice: Long,
    val estimatedDurationDays: Int,
    val proposalMessage: String
)
