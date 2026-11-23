package com.example.proyek_papb_fe.core.data.model

data class WorkPlanMilestone(
    val name: String,
    val weightPercent: Int?
)

enum class WorkPlanStatus { DRAFT, SUBMITTED, APPROVED }

val WorkPlanStatus.isLocked: Boolean get() = this != WorkPlanStatus.DRAFT

data class WorkPlan(
    val projectId: String,
    val milestones: List<WorkPlanMilestone>,
    val status: WorkPlanStatus,
    val updatedAtMillis: Long
)
