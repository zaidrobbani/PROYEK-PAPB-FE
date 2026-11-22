package com.example.proyek_papb_fe.core.data.remote.dto

data class MilestoneDto(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String?,
    /** Desimal dalam string, mis. "20.00". */
    val weightPercent: String,
    val status: String,
    val sortOrder: Int,
    val completedAt: String?,
    val updatedByUserId: String?
)

data class MilestonePlanDto(
    val projectId: String,
    val items: List<MilestoneDto>?,
    val totalWeight: String,
    val isPlanComplete: Boolean
)

data class MilestoneItemRequest(
    val name: String,
    val description: String,
    val weightPercent: Int
)

data class MilestonePlanRequest(val items: List<MilestoneItemRequest>)

data class MilestoneProgressDto(
    val projectId: String,
    val totalWeight: String,
    val completedWeight: String,
    val remainingWeight: String,
    val unallocatedWeight: String,
    val progressPercent: String,
    val totalMilestones: Int,
    val completedMilestones: Int,
    val isPlanComplete: Boolean
)

data class MilestoneDelayRiskDto(
    val projectId: String,
    val riskLevel: String,
    val elapsedDays: Int,
    val totalDays: Int,
    val expectedProgressPercent: String,
    val actualProgressPercent: String,
    val gapPercent: String
)
