package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.ProjectRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SubmitApplicationRequest
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.ProjectRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.UploadPartFactory
import com.example.proyek_papb_fe.core.domain.model.Application
import com.example.proyek_papb_fe.core.domain.model.FreelancerRecommendation
import com.example.proyek_papb_fe.core.domain.model.Page
import com.example.proyek_papb_fe.core.domain.model.Project
import com.example.proyek_papb_fe.core.domain.model.ProjectFile
import com.example.proyek_papb_fe.core.domain.model.ProjectInput
import com.example.proyek_papb_fe.core.domain.model.ProjectRecommendation
import com.example.proyek_papb_fe.core.domain.model.UploadFile

interface ProjectRepository {
    suspend fun create(input: ProjectInput): Project
    suspend fun update(projectId: String, input: ProjectInput): Project
    suspend fun delete(projectId: String)
    suspend fun detail(projectId: String): Project
    suspend fun mine(): List<Project>
    suspend fun searchOpen(
        search: String? = null,
        categoryId: String? = null,
        minBudget: Long? = null,
        maxBudget: Long? = null,
        limit: Int = 50,
        offset: Int = 0
    ): Page<Project>

    suspend fun attachments(projectId: String): List<ProjectFile>
    suspend fun uploadAttachment(projectId: String, file: UploadFile): ProjectFile
    suspend fun deleteAttachment(projectId: String, attachmentId: String)

    suspend fun apply(projectId: String, price: Long, durationDays: Int, message: String): Application
    suspend fun applicants(projectId: String): List<Application>
    suspend fun myApplications(): List<Application>

    suspend fun recommendedFreelancers(projectId: String): List<FreelancerRecommendation>
    suspend fun recommendedProjects(limit: Int = 20): List<ProjectRecommendation>
}

class DefaultProjectRepository(
    private val remote: ProjectRemoteDataSource,
    private val uploadPartFactory: UploadPartFactory
) : ProjectRepository {

    override suspend fun create(input: ProjectInput): Project = remote.create(input.toRequest()).toDomain()

    override suspend fun update(projectId: String, input: ProjectInput): Project =
        remote.update(projectId, input.toRequest()).toDomain()

    override suspend fun delete(projectId: String) {
        remote.delete(projectId)
    }

    override suspend fun detail(projectId: String): Project = remote.detail(projectId).toDomain()

    override suspend fun mine(): List<Project> = remote.mine().map { it.toDomain() }

    override suspend fun searchOpen(
        search: String?,
        categoryId: String?,
        minBudget: Long?,
        maxBudget: Long?,
        limit: Int,
        offset: Int
    ): Page<Project> {
        val page = remote.searchOpen(search?.trim()?.ifBlank { null }, categoryId, minBudget, maxBudget, limit, offset)
        return Page(page.items.orEmpty().map { it.toDomain() }, page.hasMore)
    }

    override suspend fun attachments(projectId: String): List<ProjectFile> =
        remote.attachments(projectId).map { it.toDomain() }

    override suspend fun uploadAttachment(projectId: String, file: UploadFile): ProjectFile =
        remote.uploadAttachment(projectId, uploadPartFactory.create("file", file)).toDomain()

    override suspend fun deleteAttachment(projectId: String, attachmentId: String) {
        remote.deleteAttachment(projectId, attachmentId)
    }

    override suspend fun apply(projectId: String, price: Long, durationDays: Int, message: String): Application =
        remote.apply(projectId, SubmitApplicationRequest(price, durationDays, message.trim())).toDomain()

    override suspend fun applicants(projectId: String): List<Application> =
        remote.applicants(projectId).items.orEmpty().map { it.toDomain() }

    override suspend fun myApplications(): List<Application> =
        remote.myApplications().items.orEmpty().map { it.toDomain() }

    override suspend fun recommendedFreelancers(projectId: String): List<FreelancerRecommendation> =
        remote.recommendedFreelancers(projectId).items.orEmpty().map { it.toDomain() }

    override suspend fun recommendedProjects(limit: Int): List<ProjectRecommendation> =
        nullIfNotFound { remote.recommendedProjects(limit).items.orEmpty().map { it.toDomain() } } ?: emptyList()

    private fun ProjectInput.toRequest() = ProjectRequest(
        categoryId = categoryId,
        title = title.trim(),
        description = description.trim(),
        budget = budget,
        deadline = deadline,
        skillIds = skillIds
    )
}
