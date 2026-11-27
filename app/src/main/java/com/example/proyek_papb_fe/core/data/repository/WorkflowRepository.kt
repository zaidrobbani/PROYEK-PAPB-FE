package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.MilestoneItemRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MilestonePlanRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SelectFreelancerRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UpdateAgreementRequest
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.WorkflowRemoteDataSource
import com.example.proyek_papb_fe.core.domain.model.DelayRisk
import com.example.proyek_papb_fe.core.domain.model.MilestoneDraft
import com.example.proyek_papb_fe.core.domain.model.MilestonePlan
import com.example.proyek_papb_fe.core.domain.model.MilestoneProgress
import com.example.proyek_papb_fe.core.domain.model.MyProjects
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail

interface WorkflowRepository {
    suspend fun myProjects(status: String? = null): MyProjects
    suspend fun detail(projectId: String): WorkflowDetail
    suspend fun selectFreelancer(projectId: String, applicationId: String?, freelancerId: String? = null): WorkflowDetail
    suspend fun updateAgreement(
        projectId: String,
        scopeOfWork: String? = null,
        price: Long? = null,
        deadline: String? = null,
        durationDays: Int? = null
    ): WorkflowDetail
    suspend fun approveAgreement(projectId: String, agreementVersion: Int?): WorkflowDetail
    suspend fun requestChanges(projectId: String, note: String): WorkflowDetail
    suspend fun cancel(projectId: String, reason: String, note: String?): WorkflowDetail
    suspend fun startWork(projectId: String): WorkflowDetail

    suspend fun milestones(projectId: String): MilestonePlan
    suspend fun saveMilestones(projectId: String, items: List<MilestoneDraft>): MilestonePlan
    suspend fun progress(projectId: String): MilestoneProgress
    suspend fun delayRisk(projectId: String): DelayRisk
    suspend fun completeMilestone(projectId: String, milestoneId: String): MilestoneProgress
}

class DefaultWorkflowRepository(
    private val remote: WorkflowRemoteDataSource
) : WorkflowRepository {
    override suspend fun myProjects(status: String?): MyProjects {
        val dto = remote.myProjects(status)
        return MyProjects(dto.items.orEmpty().map { it.toDomain() }, dto.statusCounts.orEmpty())
    }

    override suspend fun detail(projectId: String): WorkflowDetail = remote.detail(projectId).toDomain()

    override suspend fun selectFreelancer(projectId: String, applicationId: String?, freelancerId: String?): WorkflowDetail =
        remote.selectFreelancer(projectId, SelectFreelancerRequest(applicationId, freelancerId)).toDomain()

    override suspend fun updateAgreement(
        projectId: String,
        scopeOfWork: String?,
        price: Long?,
        deadline: String?,
        durationDays: Int?
    ): WorkflowDetail = remote.updateAgreement(
        projectId,
        UpdateAgreementRequest(scopeOfWork, price, deadline, durationDays)
    ).toDomain()

    override suspend fun approveAgreement(projectId: String, agreementVersion: Int?): WorkflowDetail =
        remote.approve(projectId, agreementVersion).toDomain()

    override suspend fun requestChanges(projectId: String, note: String): WorkflowDetail =
        remote.requestChanges(projectId, note.trim()).toDomain()

    override suspend fun cancel(projectId: String, reason: String, note: String?): WorkflowDetail =
        remote.cancel(projectId, reason, note?.trim()?.ifBlank { null }).toDomain()

    override suspend fun startWork(projectId: String): WorkflowDetail = remote.start(projectId).toDomain()

    override suspend fun milestones(projectId: String): MilestonePlan = remote.milestones(projectId).toDomain()

    override suspend fun saveMilestones(projectId: String, items: List<MilestoneDraft>): MilestonePlan =
        remote.saveMilestones(
            projectId,
            MilestonePlanRequest(items.map { MilestoneItemRequest(it.name.trim(), it.description.trim(), it.weightPercent) })
        ).toDomain()

    override suspend fun progress(projectId: String): MilestoneProgress = remote.progress(projectId).toDomain()

    override suspend fun delayRisk(projectId: String): DelayRisk = remote.delayRisk(projectId).toDomain()

    override suspend fun completeMilestone(projectId: String, milestoneId: String): MilestoneProgress =
        remote.completeMilestone(projectId, milestoneId).toDomain()
}
